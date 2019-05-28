package com.axtstar.asta4e.core

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.Date

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{CellReference}

import scala.util.matching.Regex

object ExcelBasic {

  def apply()={
    new ExcelBasic()
  }

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

class ExcelBasic extends DataCore[ExcelBasic] {

  private var ignoreSheets:List[String] = List()
  private var layoutXls:FileInputStream = null
  private var outputXls:FileOutputStream = null

  def withIgnoreSheets(_ignoreSheets:List[String])={
    this.ignoreSheets = _ignoreSheets
    this
  }

  def withLayoutXls(_layoutXls:FileInputStream)={
    this.layoutXls = _layoutXls
    this
  }

  def withOutXls(_outputXls:FileOutputStream)={
    this.outputXls = _outputXls
    this
  }

  import  com.axtstar.asta4e.core.ExcelBasic._

  /**
    * output Excel from Map
    *
    * @param dataTemplateXls Excel template File path which has ${} binder
    * @param outLayout     Output template Excel File path
    * @param outXlsPath      Output Excel path
    * @param bindData        DataBinder which consists Map of name of ${} and value
    */
  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setData(
               dataTemplateXls: String,
               outLayout: String,
               outXlsPath: String,
               bindData: (String, Map[String, Any])*
             ): Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outLayout)

    setData(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      bindData: _*
    )
  }

  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setData(
               dataTemplateXlsStream: FileInputStream,
               outLayoutStream: FileInputStream,
               outXlsPath: String,
               bindData: (String, Map[String, Any])*
             ): Unit = {
    val locationMap = getExcelLocation(dataTemplateXlsStream)

    this.withLocation(locationMap)
        .withLayoutXls(outLayoutStream)
        .withOutXls(new FileOutputStream(outXlsPath))
        .setData(bindData:_*){
          x =>
            x
        }

  }

    /**
    * output Excel from Map
    *
    * @param locationMap          Excel template File stream which has ${} binderes
    * @param outLayoutStream     Output templae Excel File stream
    * @param outXlsPath            Output Excel path
    * @param bindData              DataBinder which consists Map of name of ${} and value
    */

    @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setData(
               locationMap: List[Location],
               outLayoutStream: FileInputStream,
               outXlsPath: String,
               bindData: (String, Map[String, Any])*
             ): Unit = {

    val workbook = WorkbookFactory.create(outLayoutStream)

    val out = new FileOutputStream(new File(outXlsPath))

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

                            setOneCell(target, bindMap._1, map, x)
                          }
                        }
                      }
                  }
              }
          }
      }

      workbook.write(out)
    }
    catch{
      case ex:Throwable =>
        throw ex
    }
    finally{
      out.close()
      workbook.close()
      outLayoutStream.close()
    }
  }

  def setData(bindData: (String, Map[String, Any])*)
             (f:Map[String, Any] => Map[String, Any])={
    val workbook = WorkbookFactory.create(layoutXls)

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

      workbook.write(outputXls)
    }
    catch{
      case ex:Throwable =>
        throw ex
    }
    finally{
      outputXls.close()
      workbook.close()
      layoutXls.close()
    }
  }

  /**
    *
    * @param dataTemplateXlsStream
    * @param outLayoutStream
    * @param outXlsPath
    * @param bindData
    */
  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setDataDown(
                   dataTemplateXlsStream: FileInputStream,
                   outLayoutStream: FileInputStream,
                   outXlsPath: String,
                   bindData: (String, IndexedSeq[Map[String, Any]])*
                 ): Unit ={
    val locationMap = getExcelLocation(dataTemplateXlsStream)

    setDataDown(
      locationMap,
      outLayoutStream,
      outXlsPath,
      bindData:_*
    )

  }

  /**
    * output Excel from Map
    *
    * @param outLayoutStream     Output templae Excel File stream
    * @param outXlsPath            Output Excel path
    * @param bindData              DataBinder which consists Map of name of ${} and value
    */
  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setDataDown(
                   locationMap: List[Location],
                   outLayoutStream: FileInputStream,
                   outXlsPath: String,
                   bindData: (String, IndexedSeq[Map[String, Any]])*
                 ): Unit = {

    val workbook = WorkbookFactory.create(outLayoutStream)

    val out = new FileOutputStream(new File(outXlsPath))

    this.withLocation(locationMap)
      .withLayoutXls(outLayoutStream)
      .withOutXls(out)
      .setDataDown(bindData:_*){
        x =>
          x
      }

  }

  /**
    * output Excel from Map
    *
    * @param dataTemplateXls Excel template File path which has ${} binder
    * @param outLayout     Output template Excel File path
    * @param outXlsPath      Output Excel path
    * @param bindData        DataBinder which consists Map of name of ${} and value
    */
  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def setDataDown(
               dataTemplateXls: String,
               outLayout: String,
               outXlsPath: String,
               bindData: (String, IndexedSeq[Map[String, Any]])*
             ): Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outLayout)

    this.withLocation(getExcelLocation((dataTemplateXls)))
        .withLayoutXls(new FileInputStream(outLayout))
        .withOutXls(new FileOutputStream(outXlsPath))
        .setDataDown(bindData:_*){
          x =>
            x
        }
  }


  def setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*)
             (f:Map[String, Any] => Map[String, Any])= {
    val workbook = WorkbookFactory.create(layoutXls)

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

      workbook.write(outputXls)
    }
    catch{
      case ex:Throwable =>
        throw ex
    }
    finally{
      outputXls.close()
      workbook.close()
      layoutXls.close()
    }

  }


    @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getData(
               dataTemplateXls: String,
               inputXlsPath: String,
               ignoreSheet: List[String]
             ): IndexedSeq[(String, Map[String, Any])] = {

    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val iStream = new FileInputStream(inputXlsPath)

    getData(
      dataTemplateXlsStream,
      iStream,
      ignoreSheet
    )

  }

  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getData(
               dataTemplateXlsStream: FileInputStream,
               iStream: FileInputStream,
               ignoreSheet: List[String]
             ): IndexedSeq[(String, Map[String, Any])] = {
    val locations = getExcelLocation(dataTemplateXlsStream)

    getData(
      locations,
      iStream,
      ignoreSheet
    )
  }

  def getData[B](
                  iStream: FileInputStream
             )(f:Map[String, Any] => B)={
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

    /**
    * get databind Map from Excel
    *
    * @param dataTemplateXlsStream template Excel file stream
    * @param iStream               input Excel
    * @param ignoreSheet           ignore Sheet names
    */
    @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getData(
               locations: List[Location],
               iStream: FileInputStream,
               ignoreSheet: List[String]
             ): IndexedSeq[(String, Map[String, Any])] = {

      this.withLocation(locations)
        .withIgnoreSheets(ignoreSheet)
        .getData(iStream){
          x =>
            x
        }
  }

  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getDataDown(
               dataTemplateXls: String,
               inputXlsPath: String,
               ignoreSheet: List[String]
             ): IndexedSeq[(String, IndexedSeq[Map[String, Any]])] = {

    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val iStream = new FileInputStream(inputXlsPath)

    getDataDown(
      dataTemplateXlsStream,
      iStream,
      ignoreSheet
    )

  }

  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getDataDown(
                   dataTemplateXlsStream: FileInputStream,
                   iStream: FileInputStream,
                   ignoreSheet: List[String]
                 ): IndexedSeq[(String, IndexedSeq[Map[String, Any]])] = {
    val locations = getExcelLocation(dataTemplateXlsStream)
    this.withLocation(locations)
        .withIgnoreSheets(ignoreSheet)
        .getDataDown(iStream){
          x => x
        }
  }

    /**
    * get databind list Map from Excel
    *
    * @param dataTemplateXlsStream template Excel file stream
    * @param iStream               input Excel
    * @param ignoreSheet           ignore Sheet names
    */
  @deprecated("this method will be removed, use withXXXXX, instead", "0.0.11")
  def getDataDown(
                   locations: List[Location],
                   iStream: FileInputStream,
                   ignoreSheet: List[String]
             ): IndexedSeq[(String, IndexedSeq[Map[String, Any]])] = {
    this.withLocation(locations)
      .withIgnoreSheets(ignoreSheet)
      .getDataDown(iStream){
        x =>
          x
      }
  }


  def getDataDown[B](iStream:FileInputStream)(f:Map[String, Any] => B)={
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
