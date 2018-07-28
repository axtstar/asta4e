package com.axtstar.asta4e.core

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.Date

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{CellAddress, CellReference}

import scala.util.matching.Regex

trait ExcelBasic {
  val allReplaceBrace: Regex = "\\$\\{([^\\}]*)\\}".r

  def getBindName(bindName: String): String = {
    bindName.replaceAll("^\\$\\{", "").replaceAll("\\}$", "")
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    *
    * @param xlsPath template excel path
    * @return tuple 4
    */
  protected def getExcelLocation(xlsPath: String): List[(String, CellAddress, Cell, List[String])] = {
    val stream = new FileInputStream(xlsPath)
    getExcelLocation(stream)
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    *
    * @param stream template Excel file stream
    *
    */
  protected def getExcelLocation(stream: FileInputStream): List[(String, CellAddress, Cell, List[String])] = {

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
                (null, null, null, Nil)
              case x: Cell =>
                val all = allReplaceBrace.findAllIn(x.toString)
                var result: List[String] = Nil
                while (all.hasNext) {
                  val d = all.next()
                  result = d :: result
                }
                if (result == Nil) {
                  (null, null, null, Nil)
                } else {
                  // sheet name -> cell address -> cell(value) -> List(binder)
                  (sheet.getSheetName, cell.getAddress, cell, result.reverse)
                }
            }
          })
            .filter(_._1 != null) //ignore null
            .toList
        })
          .filter(_.nonEmpty)
          .toList
      }).flatten.flatten.toList

      results
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

  /**
    * output Excel from Map
    *
    * @param dataTemplateXls   Excel template File path which has ${} binder
    * @param outTemplate       Output template Excel File path
    * @param outXlsPath        Output Excel path
    * @param locationDataArray DataBinder which consists Map of name of ${} and value
    */
  @deprecated("use setData instead","0.0.4")
  def setDataAsTemplate(
                         dataTemplateXls: String,
                         outTemplate: String,
                         outXlsPath: String,
                         locationDataArray: Map[String, Any]*
                       ): Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outTemplate)

    setDataAsTemplate(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      locationDataArray: _*
    )
  }


  /**
    * output Excel from Map
    *
    * @param dataTemplateXlsStream Excel template File stream which has ${} binder
    * @param outTemplateStream     Output template Excel File stream
    * @param outXlsPath            Output Excel path
    * @param locationDataArray     DataBinder which consists Map of name of ${} and value
    */
  @deprecated("use setData instead","0.0.4")
  def setDataAsTemplate(
                         dataTemplateXlsStream: FileInputStream,
                         outTemplateStream: FileInputStream,
                         outXlsPath: String,
                         locationDataArray: Map[String, Any]*
                       ): Unit = {
    var index = 0
    setData(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      locationDataArray.map {
        x =>
          index = index + 1
          s"Sheet$index" -> x
      }: _*
    )
  }

  /**
    * output Excel from Map
    *
    * @param dataTemplateXls Excel template File path which has ${} binder
    * @param outTemplate     Output template Excel File path
    * @param outXlsPath      Output Excel path
    * @param bindData        DataBinder which consists Map of name of ${} and value
    */
  def setData(
               dataTemplateXls: String,
               outTemplate: String,
               outXlsPath: String,
               bindData: (String, Map[String, Any])*
             ): Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outTemplate)

    setData(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      bindData: _*
    )
  }

  /**
    * output Excel from Map
    *
    * @param dataTemplateXlsStream Excel template File stream which has ${} binderes
    * @param outTemplateStream     Output templae Excel File stream
    * @param outXlsPath            Output Excel path
    * @param bindData              DataBinder which consists Map of name of ${} and value
    */
  def setData(
               dataTemplateXlsStream: FileInputStream,
               outTemplateStream: FileInputStream,
               outXlsPath: String,
               bindData: (String, Map[String, Any])*
             ): Unit = {
    val locationMap = getExcelLocation(dataTemplateXlsStream)

    val workbook = WorkbookFactory.create(outTemplateStream)

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
        bindDataASheet: (String, Map[String, Any]) =>
          //determine sheet
          val sheet = workbook.getSheet(bindDataASheet._1)

          bindDataASheet._2.foreach {
            bindMap =>

              locationMap.filter(
                p =>
                  p._4.contains("${" + bindMap._1 + "}")
              ).foreach {
                x =>
                  //iterate ${}
                  x._4.foreach {
                    xx => {
                      if (bindDataASheet._2.exists(
                        p =>
                          "${" + s"${p._1}" + "}" == xx)
                      ) {
                        val ref = new CellReference(x._2.toString)
                        val row = sheet.getRow(ref.getRow)
                        val target = row.getCell(ref.getCol)

                        target.getCellTypeEnum match {
                          case CellType.NUMERIC =>
                            bindMap._2 match {
                              case null =>
                                target.setCellType(CellType.BLANK)
                              case tiny: Date =>
                                target.setCellValue(tiny)
                              case tiny: Integer =>
                                target.setCellValue(tiny.toDouble)
                              case _ =>
                                target.setCellValue(bindMap._2.asInstanceOf[Double])
                            }

                          case CellType.BOOLEAN =>
                            target.setCellValue(if (bindMap._2 == null) null.asInstanceOf[Boolean] else bindMap._2.asInstanceOf[Boolean])

                          case CellType.FORMULA =>
                            target.setCellValue(if (bindMap._2 == null) null.asInstanceOf[String] else bindMap._2.toString)

                          case _ =>
                            val alt = bindDataASheet._2.foldLeft(x._3.toString) {
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
      outTemplateStream.close()
    }
  }

  /**
    * get data bind Map from Excel
    *
    * @param dataTemplateXls template Excel file path
    * @param inputXlsPath    input Excel file path
    * @param ignoreSheet     ignore sheet names
    * @return Map
    */
  def getDataAsTemplate(
                         dataTemplateXls: String,
                         inputXlsPath: String,
                         ignoreSheet: List[String] = List()
                       ): List[Map[String, Any]] = {

    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val iStream = new FileInputStream(inputXlsPath)

    getDataAsTemplate(
      dataTemplateXlsStream,
      iStream,
      ignoreSheet
    )
  }

  def getDataAsTemplate(
                         dataTemplateXls: FileInputStream,
                         iStream: FileInputStream,
                         ignoreSheet: List[String]

                       ): List[Map[String, Any]] = {
    val target = getData(
      dataTemplateXls,
      iStream,
      ignoreSheet
    )
    target.map {
      x =>
        x._2
    }.toList
  }

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


  /**
    * get databind Map from Excel
    *
    * @param dataTemplateXlsStream template Excel file stream
    * @param iStream               input Excel
    * @param ignoreSheet           ignore Sheet names
    */
  def getData(
               dataTemplateXlsStream: FileInputStream,
               iStream: FileInputStream,
               ignoreSheet: List[String]
             ): IndexedSeq[(String, Map[String, Any])] = {
    val locations = getExcelLocation(dataTemplateXlsStream)

    val workbook = WorkbookFactory.create(iStream)

    try {

      val result = (for (index <- 0 until workbook.getNumberOfSheets) yield {
        val sheet = workbook.getSheetAt(index)

        if (ignoreSheet.contains(sheet.getSheetName)) {
          null
        } else {

          var results = scala.collection.immutable.Map[String, Any]()


          locations.foreach {
            x =>
              //target cell
              val target = {
                val ref = new CellReference(x._2.toString)
                val row = sheet.getRow(ref.getRow)

                if (ref == null || row == null) {
                  null
                }
                else {
                  row.getCell(ref.getCol)
                }
              }


              target match {
                case null =>
                  x._4.foreach {
                    xx =>
                      results += (getBindName(xx) -> null)
                  }
                case xx: Cell =>
                  xx.getCellTypeEnum match {
                    case CellType.NUMERIC =>
                      val format = ExcelNumberFormat.from(xx.getCellStyle)
                      if (DateUtil.isADateFormat(format)) {
                        x._4.foreach {
                          xxx =>
                            results += (getBindName(xxx) -> xx.getDateCellValue)
                        }
                      } else {
                        x._4.foreach {
                          xxx =>
                            results += (getBindName(xxx) -> xx.getNumericCellValue)
                        }
                      }

                    case CellType.BOOLEAN =>
                      x._4.foreach {
                        xxx =>
                          results += (getBindName(xxx) -> xx.getBooleanCellValue)
                      }

                    case CellType.BLANK =>
                      x._4.foreach {
                        xxx =>
                          results += (getBindName(xxx) -> null)
                      }

                    case CellType._NONE =>
                      x._4.foreach {
                        xxx =>
                          results += (getBindName(xxx) -> null)
                      }

                    case CellType.STRING | CellType.FORMULA =>
                      //construct regular expression from a template cell
                      //consider multiple binder like `${id1}-${id2}`
                      val regEx = ("(?s)" + x._4.foldLeft(if (x._3 == null) {
                        ""
                      } else {
                        x._3.toString
                      }) {
                        (acc, xx) =>
                          val xxx = xx.replace("$", "\\$")
                            .replace("{", "\\{")
                            .replace("}", "\\}")
                          acc.replaceFirst(xxx, "(.+)")
                      }).r

                      val all = regEx.findFirstMatchIn(xx.getStringCellValue)

                      if (all.isDefined) {
                        for (i <- 0 until all.get.groupCount) {
                          results += getBindName(x._4(i)) -> all.get.group(i + 1)
                        }
                      }
                      else {
                        x._4.foreach {
                          xx =>
                            results += getBindName(xx) -> null
                        }
                      }

                    case _ =>
                      throw new IllegalArgumentException

                  }
              }
          }
          sheet.getSheetName -> results
        }
      }).filter(_ != null)

      result

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
