package com.axtstar.asta4e.basic

import java.io.{File, FileInputStream}
import java.util.Date

import com.axtstar.asta4e.core.{DataCore, InitialCore}
import com.axtstar.asta4e.etc.Location
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.util.Try
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
                //Origin Name
                var result: List[(String,String)] = Nil
                while (all.hasNext) {
                  val d = all.next()

                  val origin = d
                  val name = getBindName(d)

                  result = (name, origin) :: result
                }
                if (result == Nil) {
                  None
                } else {
                  //
                  val regEx = Try(
                    Option(
                    ("(?s)" + result.foldLeft(x.getStringCellValue) {
                    (acc,
                     xx) =>
                      val xxx = xx._2.replace("$", "\\$")
                        .replace("{", "\\{")
                        .replace("}", "\\}")
                      acc.replaceFirst(xxx, "(.+)")
                  }).r)
                  ).getOrElse(None)

                  Option(Location(
                    name=result(0)._1,
                    original=cell.getStringCellValue,
                    positionX = cell.getColumnIndex,
                    positionY = cell.getRowIndex,
                    expression = regEx,
                    bindNames = result.reverse.map(_._1)
                  ))
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
      stream.close()
    }
  }

  def generateExcel2Location(path: String):String = {
    val f = new File(path)
    val stream = new FileInputStream(f.getAbsolutePath)

    try {
      s"""import com.axtstar.asta4e.etc.Location
         |
         |val ${f.getName.split('.')(0)} = ${generateExcel2Location(stream)}""".stripMargin
    }
    catch {
      case ex: Throwable =>
        throw ex
    }
    finally {
      stream.close()
    }
  }

  def generateExcel2Location(stream: FileInputStream):String = {
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
                //Origin Name
                var result: List[(String, String)] = Nil
                while (all.hasNext) {
                  val d = all.next()

                  val origin = d
                  val name = getBindName(d)

                  result = (name, origin) :: result
                }
                if (result == Nil) {
                  None
                } else {
                  //
                  val regEx = Try(
                    Option(
                      ("(?s)" + result.foldLeft(x.getStringCellValue) {
                        (acc,
                         xx) =>
                          val xxx = xx._2.replace("$", "\\$")
                            .replace("{", "\\{")
                            .replace("}", "\\}")
                          acc.replaceFirst(xxx, "(.+)")
                      }).r)
                  ).getOrElse(None)

                  Option(
                    s"""|  Location(
                        |    name = "${result(0)._1}",
                        |    original = "${cell.getStringCellValue}",
                        |    positionX = ${cell.getColumnIndex},
                        |    positionY = ${cell.getRowIndex},
                        |    expression = ${
                      regEx match {
                        case None =>
                          "None"
                        case Some(x) =>
                          s"""Some("${x.pattern}".r)"""
                      }
                    },
                        |    bindNames = ${result.reverse.map("\"" + _._1 + "\"")}
                        |  )""".stripMargin)
                }
            }
          })
            .filter(_ != None) //ignore null
            .toList
        })
          .filter(_.nonEmpty)
          .toList
      }).flatten.flatten.toList

      s"List(\n${results.map(_.get).mkString(",\n")}\n)"
    }
    catch {
      case ex: Throwable =>
        throw ex
    }
    finally {
      stream.close()
    }
  }


  def getOneCell(target:Cell, x:Location):Map[String, Any]= {

    target match {
      case null =>
          Map(x.name -> null)
      case xx: Cell =>
        xx.getCellType match {
          case CellType.NUMERIC =>
            val format = ExcelNumberFormat.from(xx.getCellStyle)
            if (DateUtil.isADateFormat(format)) {
                  Map(x.name -> xx.getDateCellValue)
            } else {
                  Map(x.name -> xx.getNumericCellValue)
            }

          case CellType.BOOLEAN =>
                Map(x.name -> xx.getBooleanCellValue)

          case CellType.BLANK =>
                Map(x.name -> null)

          case CellType._NONE =>
                Map(x.name -> null)

          case CellType.STRING =>
            //construct regular expression from a template cell
            //consider multiple binder like `${id1}-${id2}`
            val regEx = x.expression.get

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
                Map(x.name -> (
                  target.getCachedFormulaResultType match {
                    case CellType.NUMERIC =>
                      target.getNumericCellValue
                    case CellType.STRING =>
                      target.getStringCellValue
                    case _ =>
                      null
                  }))

          case _ =>
            throw new IllegalArgumentException

        }
    }
  }

  def setOneCell(target:Cell, maps: Map[String, Any], x:Location)={
    val bindData = maps(x.name)
    target.getCellType match {
      case CellType.NUMERIC =>
        bindData match {
          case null =>
            target.setBlank()
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
        val alt = x.bindNames.foldLeft(x.original) {
          (acc, x) =>
            val bindName = getBindName(x)
            acc.replaceAll("\\$\\{" + s"${bindName}" + "\\}", maps(bindName).toString)
        }
        target.setCellValue(alt)
    }
  }

}

trait ExcelBasic extends DataCore with InitialCore[ExcelBasic] {

  import ExcelBasic._

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

  override def _setData(bindData: (String, Map[String, Any])*) ={
    val workbook = if(layoutStram==null){
      val target = new XSSFWorkbook()
      target.createSheet()
      target
    } else {
      WorkbookFactory.create(layoutStram)
    }

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
                      p.bindNames.contains(bindMap._1)
                  ).foreach {
                    x =>
                      //iterate ${}
                      x.bindNames.foreach {
                        xx => {
                          if (map.exists(
                            p =>
                              p._1 == xx)
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

                            setOneCell(target, map, x)
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
      outputStream.flush()
      outputStream.close()
      if(layoutStram!=null){
        layoutStram.close()
      }
    }
  }

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*) = {
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
                              p.bindNames.contains(bindMap._1)
                          ).foreach {
                            x =>
                              //iterate ${}
                              x.bindNames.foreach {
                                xx => {
                                  if (map.exists(
                                    p =>
                                      p._1 == xx)
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

                                    setOneCell(target, map, x)
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
      outputStream.flush()
      outputStream.close()
      layoutStram.close()
    }

  }

  override def _getData(iStream: FileInputStream):IndexedSeq[(String,Map[String,Any])]={
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
        sheet.getSheetName -> result.toMap
      }
    } catch {
      case ex: Throwable =>
        throw ex
    }
    finally {
      iStream.close()
    }
 }

  override def _getDataDown(iStream:FileInputStream):IndexedSeq[(String, IndexedSeq[Map[String, Any]])]={
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

            results
          })
        }
      }).filter(_ != null)

    } catch {
      case ex:Throwable =>
        throw ex
    }
    finally{
      iStream.close()
    }

  }

}
