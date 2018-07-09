package com.axtstar.asta4e

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{CellAddress, CellReference}

object ExcelMapper {

  val allReplaceBrace = "\\$\\{([^\\}]*)\\}".r

  def getBindName(bindName:String)={
    bindName.replaceAll("^\\$\\{", "").replaceAll("\\}$", "")
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    * @param xlsPath template excel path
    * @return tuple 4
    */
  def getExcelLocation(xlsPath:String):List[(String, CellAddress, Cell, List[String])] = {
    val stream = new FileInputStream(xlsPath)
    getExcelLocation(stream)
  }

  /**
    * search ${} from Excel file and returen to tuple 4
    * @param stream template Excel file stream
    *
    */
  def getExcelLocation(stream:FileInputStream) = {

    val workbook = WorkbookFactory.create(stream)

    val results = (for (i <- 0 to workbook.getNumberOfSheets - 1) yield {
      val sheet = workbook.getSheetAt(i)
      (for (rowID <- 0 to sheet.getLastRowNum) yield {
        val row = sheet.getRow(rowID)
        (for (columnID <- 0 to row.getLastCellNum) yield {
          val cell = row.getCell(columnID)
          cell match {
            case null =>
              (null, null, null, Nil)
            case x:Cell =>
              val all = allReplaceBrace.findAllIn(x.toString)
              var result:List[String] = Nil
              while(all.hasNext){
                val d = all.next()
                result = d :: result
              }
              if (result==Nil){
                (null, null, null, Nil)
              }else {
                // sheet name -> cell address -> cell(value) -> List(binder)
                (sheet.getSheetName, cell.getAddress, cell, result.reverse)
              }
          }
        })
          .filter(_._1!=null) //ignore null
          .toList
      })
        .filter(_.size > 0)
        .toList
    }).flatten.flatten.toList

    workbook.close()

    results
  }

  /**
    * output Excel from Map
    * @param dataTemplateXls
    * @param outTemplate
    * @param outXlsPath
    * @param locationDataArray
    */
//  @deprecated("use setData instead","0.0.3")
  def setDataAsTemplate(
                         dataTemplateXls:String,
                         outTemplate:String,
                         outXlsPath:String,
                         locationDataArray:Map[String,Any] *
                       ):Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outTemplate)

    setDataAsTemplate(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      locationDataArray:_*
    )
  }


  /**
    * output Excel from Map
    * @param dataTemplateXlsStream Excel template File stream which has ${} binderes
    * @param outTemplateStream Output templae Excel File stream
    * @param outXlsPath Output Excel path
    * @param locationDataArray DataBinder which consists Map of name of ${} and value  
    */
//  @deprecated("use setData instead","0.0.3")
  def setDataAsTemplate(
                         dataTemplateXlsStream:FileInputStream,
                         outTemplateStream:FileInputStream,
                         outXlsPath:String,
                         locationDataArray:Map[String,Any] *
                       ):Unit={
    var index = 0
    setData(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      locationDataArray.map {
        x =>
          index = index + 1
          s"Sheet${index}" -> x
      } :_*
    )
  }

  /**
    * output Excel from Map
    * @param dataTemplateXls
    * @param outTemplate
    * @param outXlsPath
    * @param bindData
    */
  def setData(
               dataTemplateXls:String,
               outTemplate:String,
               outXlsPath:String,
               bindData:(String, Map[String,Any]) *
             ):Unit = {
    val dataTemplateXlsStream = new FileInputStream(dataTemplateXls)
    val outTemplateStream = new FileInputStream(outTemplate)

    setData(
      dataTemplateXlsStream,
      outTemplateStream,
      outXlsPath,
      bindData:_*
    )
  }

  /**
    * output Excel from Map
    * @param dataTemplateXlsStream Excel template File stream which has ${} binderes
    * @param outTemplateStream Output templae Excel File stream
    * @param outXlsPath Output Excel path
    * @param bindData DataBinder which consists Map of name of ${} and value
    */
  def setData(
               dataTemplateXlsStream:FileInputStream,
               outTemplateStream:FileInputStream,
               outXlsPath:String,
               bindData:(String, Map[String,Any]) *
             ):Unit={
    val locationMap = getExcelLocation(dataTemplateXlsStream)

    val workbook = WorkbookFactory.create(outTemplateStream)

    //check sheet names
    val sheetNames = for (i <- 0 until workbook.getNumberOfSheets) yield {
      (i,workbook.getSheetAt(i).getSheetName)
    }

    //Clone Sheet if not exists
    bindData.foreach{
      sheetMap =>
        if(sheetNames.filter(_._2==sheetMap._1).size ==0){
          val sheet = workbook.cloneSheet(0)
          workbook.setSheetName(workbook.getNumberOfSheets - 1 , sheetMap._1)
        }
    }

    bindData.foreach {
      sheetMap:(String, Map[String, Any]) =>
        //determine sheet
        val sheet = workbook.getSheet(sheetMap._1)

        sheetMap._2.foreach {
          locationData =>

            locationMap.foreach {
              x =>
                //iterate ${}
                x._4.foreach {
                  xx => {
                    val ref = new CellReference(x._2.toString)
                    val row = sheet.getRow(ref.getRow)
                    val target = row.getCell(ref.getCol)

                    target.setCellValue(
                      sheetMap._2
                        .foldLeft(x._3.toString) {
                          (acc, xxx) =>
                            val alt = if (xxx._2 == null) {
                              ""
                            } else {
                              xxx._2.toString
                            }

                            acc.replaceAll("\\$\\{" + s"${xxx._1}" + "\\}", alt)

                        }
                    )
                  }
                }
            }
        }
    }

    val w = new File(outXlsPath)
    val out = new FileOutputStream(w)

    workbook.write(out)
    workbook.close()
  }


  /**
    * get databind Map from Excel
    * @param dataTemplateXls
    * @param inputXlsPath
    * @param ignoreSheet
    * @return
    */
  def getDataAsTemplate(
                         dataTemplateXls:String,
                         inputXlsPath:String,
                         ignoreSheet:List[String]=List()
                       ):List[Map[String, Any]] = {
    val stream = new FileInputStream(inputXlsPath)

    getDataAsTemplate(
      dataTemplateXls,
      stream,
      ignoreSheet
    )
  }

  def getDataAsTemplate(
               dataTemplateXls:String,
               iStream:FileInputStream,
               ignoreSheet:List[String]

             )={
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


    /**
    * get databind Map from Excel
    * @param dataTemplateXls template Excel file path
    * @param iStream input Excel
    * @param ignoreSheet ignore Sheet names
    */
  def getData(
                         dataTemplateXls:String,
                         iStream:FileInputStream,
                         ignoreSheet:List[String]
                       )={
    val locations = getExcelLocation(dataTemplateXls)

    val f = iStream
    val workbook = WorkbookFactory.create(f)

    val result = (for (index <- 0 until workbook.getNumberOfSheets)  yield {
      val sheet = workbook.getSheetAt(index)

      if(ignoreSheet.contains(sheet.getSheetName)) {
        null
      } else {

        var results = scala.collection.immutable.Map[String,Any]()


        locations.map {
          x =>
            //target cell
            val target = {
              val ref = new CellReference(x._2.toString)
              val row = sheet.getRow(ref.getRow)

              if (ref==null || row==null){
                null
              }
              else {
                row.getCell(ref.getCol)
              }
            }


            target match {
              case null =>
                x._4.map {
                  xx =>
                    results += (getBindName(xx) -> null)
                }
              case xx:Cell =>
                xx.getCellTypeEnum match {
                  case CellType.NUMERIC =>
                    val format = ExcelNumberFormat.from(xx.getCellStyle)
                    if(DateUtil.isADateFormat(format)){
                      x._4.map {
                        xxx =>
                          results += (getBindName(xxx) -> xx.getDateCellValue)
                      }
                    } else {
                      x._4.map {
                        xxx =>
                          results += (getBindName(xxx) -> xx.getNumericCellValue)
                      }
                    }

                  case (CellType.STRING | CellType.FORMULA) =>
                    //construct regular expression from a template cell
                    //consider multiple binder like `${id1}-${id2}`
                    val regEx = ("(?s)" + x._4.foldLeft(if (x._3==null){""}else{x._3.toString}) {
                      (acc, xx) =>
                        val xxx = xx.replace("$", "\\$")
                          .replace("{", "\\{")
                          .replace("}", "\\}")
                        acc.replaceFirst(xxx, "(.+)")
                    }).r

                    val all = regEx.findFirstMatchIn(xx.getStringCellValue)

                    if (all.size>0) {
                      (for (i <- 0 until all.get.groupCount) yield {
                        results += (getBindName(x._4(i)) -> all.get.group(i + 1))
                      })
                    }
                    else {
                      (x._4.map {
                        xx =>
                          results += (getBindName(xx) -> null)
                      })
                    }

                  case _ =>

                    val g = ""

                }
            }
        }
       sheet.getSheetName -> results
      }
    }).filter(_!=null)

    workbook.close()
    result
  }
}
