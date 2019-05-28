package com.axtstar.asta4e.core

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.Date

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{CellReference}

import scala.util.matching.Regex

object ExcelBasic {

  private val allReplaceBrace: Regex = "\\$\\{([^\\}]*)\\}".r

  def getBindName(bindName: String): String = {
    bindName.replaceAll("^\\$\\{", "").replaceAll("\\}$", "")
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    *
    * @param xlsPath template excel path
    * @return tuple 4
    */
  def getExcelLocation(xlsPath: String): List[Location] = {
    val stream = new FileInputStream(xlsPath)
    getExcelLocation(stream)
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    *
    * @param stream template Excel file stream
    *
    */
  //List[(String, CellAddress, Cell, List[String])]
  def getExcelLocation(stream: FileInputStream): List[Location] = {

    val workbook = WorkbookFactory.create(stream)

    try {

      val results = (for (i <- 0 until workbook.getNumberOfSheets) yield {
        val sheet = workbook.getSheetAt(i)
        (for (rowID <- 0 to sheet.getLastRowNum) yield {
          val row = sheet.getRow(rowID)
          (for (columnID <- 0 to row.getLastCellNum) yield {
            val cell = row.getCell(columnID)
            cell match {
              case null =>
                None
              case x: Cell =>
                val all = allReplaceBrace.findAllIn(x.toString)
                var result: List[String] = Nil
                while (all.hasNext) {
                  val d = all.next()
                  result = d :: result
                }
                if (result == Nil) {
                  None
                } else {
                  // sheet name -> cell address -> cell(value) -> List(binder)
                  Option(Location(name=cell.getStringCellValue, positionX = cell.getColumnIndex, positionY = cell.getRowIndex, bindNames = result.reverse))
                }
            }
          })
            .filter(_ != None) //ignore null
            .toList
        })
          .filter(_.nonEmpty)
          .toList
      }).flatten.flatten.toList

      results map (_.get)
    }
    catch {
      case ex:Throwable =>
        throw ex
    }
    finally{
      workbook.close()
      stream.close()
    }
  }

  def getOneCell(target:Cell, x:Location):Map[String, Any]= {

    target match {
      case null =>
        x.bindNames.map {
          xx =>
            (getBindName(xx) -> null)
        }.toMap
      case xx: Cell =>
        xx.getCellTypeEnum match {
          case CellType.NUMERIC =>
            val format = ExcelNumberFormat.from(xx.getCellStyle)
            if (DateUtil.isADateFormat(format)) {
              x.bindNames.map {
                xxx =>
                  (getBindName(xxx) -> xx.getDateCellValue)
              }.toMap
            } else {
              x.bindNames.map {
                xxx =>
                  (getBindName(xxx) -> xx.getNumericCellValue)
              }.toMap
            }

          case CellType.BOOLEAN =>
            x.bindNames.map {
              xxx =>
                (getBindName(xxx) -> xx.getBooleanCellValue)
            }.toMap

          case CellType.BLANK =>
            x.bindNames.map {
              xxx =>
                (getBindName(xxx) -> null)
            }.toMap

          case CellType._NONE =>
            x.bindNames.map {
              xxx =>
                (getBindName(xxx) -> null)
            }.toMap

          case CellType.STRING =>
            //construct regular expression from a template cell
            //consider multiple binder like `${id1}-${id2}`
            val regEx = ("(?s)" + x.bindNames.foldLeft(if (x.name == "") {
              ""
            } else {
              x.name
            }) {
              (acc, xx) =>
                val xxx = xx.replace("$", "\\$")
                  .replace("{", "\\{")
                  .replace("}", "\\}")
                acc.replaceFirst(xxx, "(.+)")
            }).r

            val all = regEx.findFirstMatchIn(xx.getStringCellValue)

            if (all.isDefined) {
              (for (i <- 0 until all.get.groupCount) yield{
                getBindName(x.bindNames(i)) -> all.get.group(i + 1)
              }).toMap
            }
            else {
              x.bindNames.map {
                xx =>
                  (getBindName(xx) -> null)
              }.toMap
            }

          case CellType.FORMULA =>
            x.bindNames.map {
              xx =>
                (getBindName(xx) -> (
                  target.getCachedFormulaResultTypeEnum match {
                    case CellType.NUMERIC =>
                      target.getNumericCellValue
                    case CellType.STRING =>
                      target.getStringCellValue
                    case _ =>
                      null
                  }))
            }.toMap

          case _ =>
            throw new IllegalArgumentException

        }
    }
  }

  def setOneCell(target:Cell, name: String, maps: Map[String, Any], x:Location)={
    val bindData = maps(name)
    target.getCellTypeEnum match {
      case CellType.NUMERIC =>
        bindData match {
          case null =>
            target.setCellType(CellType.BLANK)
          case tiny: Date =>
            target.setCellValue(tiny)
          case tiny: Integer =>
            target.setCellValue(tiny.toDouble)
          case _ =>
            target.setCellValue(bindData.asInstanceOf[Double])
        }

      case CellType.BOOLEAN =>
        target.setCellValue(if (bindData == null) null.asInstanceOf[Boolean] else bindData.asInstanceOf[Boolean])

      case CellType.FORMULA =>
        target.setCellValue(if (bindData == null) null.asInstanceOf[String] else bindData.toString)

      case _ =>
        val alt = maps.foldLeft(x.name) {
          (acc, xxx) =>
            val alt = if (xxx._2 == null) {
              ""
            } else {
              xxx._2.toString
            }
            acc.replaceAll("\\$\\{" + s"${xxx._1}" + "\\}", alt)
        }
        target.setCellValue(alt)
    }
  }

}

trait ExcelBasic extends DataCore[ExcelBasic] with InitialCore[ExcelBasic] {

  import  com.axtstar.asta4e.core.ExcelBasic._

  /**
    *  set sheet names which doesn't include at retrieving Excel
    * @param _ignoreSheets
    * @return ExcelBasic(this)
    */
  def withIgnoreSheets(_ignoreSheets:List[String])={
    this.ignoreSheets = _ignoreSheets
    this
  }

  /**
    * set layout excel as FileInputStream at writing excel
    * @param _layoutXls
    * @return ExcelBasic(this)
    */
  def withLayoutXls(_layoutXls:FileInputStream)={
    this.layoutStram = _layoutXls
    this
  }

  override def _setData(bindData: (String, Map[String, Any])*)
             (f:Map[String, Any] => Map[String, Any])={
    val workbook = WorkbookFactory.create(layoutStram)

    try {

      //check sheet names
      val sheetNames = for (i <- 0 until workbook.getNumberOfSheets) yield {
        (i, workbook.getSheetAt(i).getSheetName)
      }

      //Clone Sheet if not exists
      bindData.foreach {
        sheetMap =>
          if (!sheetNames.exists(_._2 == sheetMap._1)) {
            workbook.cloneSheet(0)
            workbook.setSheetName(workbook.getNumberOfSheets - 1, sheetMap._1)
          }
      }

      bindData.foreach {
        bindDataASheet =>
          bindDataASheet match {
            case (sheetName:String, map:Map[String, Any]) =>

              //determine sheet
              val sheet = workbook.getSheet(sheetName)

              map.foreach {
                bindMap =>

                  locationMap.filter(
                    p =>
                      p.bindNames.contains("${" + bindMap._1 + "}")
                  ).foreach {
                    x =>
                      //iterate ${}
                      x.bindNames.foreach {
                        xx => {
                          if (map.exists(
                            p =>
                              "${" + s"${p._1}" + "}" == xx)
                          ) {
                            val ref = new CellReference(x.positionY, x.positionX)
                            var row = sheet.getRow(ref.getRow)
                            if (row==null){
                              sheet.createRow(ref.getRow)
                              row = sheet.getRow(ref.getRow)
                            }

                            var target = row.getCell(ref.getCol)
                            if(target==null){
                              row.createCell(ref.getCol)
                              target = row.getCell(ref.getCol)
                            }

                            setOneCell(target, bindMap._1, f(map), x)
                          }
                        }
                      }
                  }
              }
          }
      }

      workbook.write(outputStream)
    }
    catch{
      case ex:Throwable =>
        throw ex
    }
    finally{
      outputStream.close()
      workbook.close()
      layoutStram.close()
    }
  }

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*)
             (f:Map[String, Any] => Map[String, Any])= {
    val workbook = WorkbookFactory.create(layoutStram)

    try {

      //check sheet names
      val sheetNames = for (i <- 0 until workbook.getNumberOfSheets) yield {
        (i, workbook.getSheetAt(i).getSheetName)
      }

      //Clone Sheet if not exists
      bindData.foreach {
        sheetMap =>
          if (!sheetNames.exists(_._2 == sheetMap._1)) {
            workbook.cloneSheet(0)
            workbook.setSheetName(workbook.getNumberOfSheets - 1, sheetMap._1)
          }
      }

      bindData.foreach {
        bindDataASheet =>
          bindDataASheet match {
            case (sheetName:String, maps:IndexedSeq[Map[String, Any]]) =>

              //determine sheet
              val sheet = workbook.getSheet(sheetName)
              maps.zipWithIndex.foreach {
                mapZip =>
                  mapZip match {
                    case (map:Map[String, Any], index:Int) =>
                      map.foreach {
                        bindMap =>

                          locationMap.filter(
                            p =>
                              p.bindNames.contains("${" + bindMap._1 + "}")
                          ).foreach {
                            x =>
                              //iterate ${}
                              x.bindNames.foreach {
                                xx => {
                                  if (map.exists(
                                    p =>
                                      "${" + s"${p._1}" + "}" == xx)
                                  ) {
                                    val ref = new CellReference(x.positionY, x.positionX)
                                    var row = sheet.getRow(ref.getRow + index)
                                    if (row==null){
                                      sheet.createRow(ref.getRow + index)
                                      row = sheet.getRow(ref.getRow + index)
                                    }
                                    var target = row.getCell(ref.getCol)
                                    if(target==null){
                                      row.createCell(ref.getCol)
                                      target = row.getCell(ref.getCol)
                                    }

                                    setOneCell(target, bindMap._1, f(map), x)
                                  }
                                }
                              }
                          }
                      }
                  }
              }
          }
      }

      workbook.write(outputStream)
    }
    catch{
      case ex:Throwable =>
        throw ex
    }
    finally{
      outputStream.close()
      workbook.close()
      layoutStram.close()
    }

  }

  override def _getData[B](
                  iStream: FileInputStream
             )(f:Map[String, Any] => B):IndexedSeq[(String,B)]={
    val workbook = WorkbookFactory.create(iStream)

    try {

      for (i <- 0 until workbook.getNumberOfSheets) yield {
        val sheet = workbook.getSheetAt(i)
        val result = locationMap.flatMap {
          x =>
            //target cell
            val target = {
              val ref = new CellReference(x.positionY, x.positionX)
              val row = sheet.getRow(ref.getRow)

              if (ref == null || row == null) {
                null
              }
              else {
                row.getCell(ref.getCol)
              }
            }
            getOneCell(target, x)
        }
        sheet.getSheetName -> f(result.toMap)
      }
    } catch {
      case ex: Throwable =>
        throw ex
    }
    finally {
      workbook.close()
      iStream.close()
    }
 }

  override def _getDataDown[B](iStream:FileInputStream)
                    (f:Map[String, Any] => B):IndexedSeq[(String, IndexedSeq[B])]={
    val workbook = WorkbookFactory.create(iStream)
    try {
      (for (index <- 0 until workbook.getNumberOfSheets) yield {
        val sheet = workbook.getSheetAt(index)

        if (ignoreSheets.contains(sheet.getSheetName)) {
          null
        } else {
          val maxRowDef = locationMap.maxBy(_.positionY).positionY
          val minRow = 0
          val maxRow = sheet.getLastRowNum() - maxRowDef
          sheet.getSheetName -> (for (i <- minRow to maxRow ) yield {
            val results = (locationMap.flatMap {
              x =>
                //target cell
                val target = {
                  val ref = new CellReference(x.positionY, x.positionX)
                  val row = sheet.getRow(ref.getRow + i)

                  if (ref == null || row == null) {
                    null
                  }
                  else {
                    row.getCell(ref.getCol)
                  }
                }
                val result = getOneCell(target, x)
                result
            }).toMap

            f(results)
          })
        }
      }).filter(_ != null)

    } catch {
      case ex:Throwable =>
        throw ex
    }
    finally{
      workbook.close()
      iStream.close()
    }

  }

}
