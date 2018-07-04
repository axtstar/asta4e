package com.axtstar.asta4e

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.poi.ss.usermodel.{Cell, CellType, WorkbookFactory}
import org.apache.poi.ss.util.{CellAddress, CellReference}

object ExcelMapper {

  val allReplaceBrace = "\\$\\{([^\\}]*)\\}".r

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
    * output Excel
    * @param dataTemplateXlsStream Excel template File stream which has ${} binderes
    * @param outTemplateStream Output templae Excel File stream
    * @param outXlsPath Output Excel path
    * @param locationDataArray DataBinder which consists Map of name of ${} and value  
    */
  def setDataAsTemplate(
                         dataTemplateXlsStream:FileInputStream,
                         outTemplateStream:FileInputStream,
                         outXlsPath:String,
                         locationDataArray:Map[String,Any] *
                       ):Unit={
    val locations = getExcelLocation(dataTemplateXlsStream)

    val workbook = WorkbookFactory.create(outTemplateStream)

    var sheetIndex = 0
    locationDataArray.map {
      locationData =>

        //determine sheet
        val sheet = if (sheetIndex==0) {
          workbook.getSheet(locations.head._1)
        }
        else{
          //copy sheet
          val sheet = workbook.cloneSheet(0)
          sheet
        }

        locations.foreach {
          x =>
            //iterate ${}
            x._4.foreach {
              xx => {
                val ref = new CellReference(x._2.toString)
                val row = sheet.getRow(ref.getRow)
                val target = row.getCell(ref.getCol)

                target.setCellValue(
                  locationData
                    .foldLeft(x._3.toString) {
                      (acc, xxx) =>
                        val alt = if (xxx._2==null){
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
        sheetIndex = sheetIndex + 1
    }

    val w = new File(outXlsPath)
    val out = new FileOutputStream(w)

    workbook.write(out)
    workbook.close()
  }

  def getDataAsTemplate(
                         dataTemplateXls:String,
                         inputXlsPath:String,
                         ignoreSheet:List[String]=List("設定")
                       ):List[Map[String, Any]] = {
    val stream = new FileInputStream(inputXlsPath)

    getDataAsTemplate(
      dataTemplateXls,
      stream,
      ignoreSheet
    )
  }



    /**
    * get databind
    * @param dataTemplateXls template
    * @param istream input Excel
    */
  def getDataAsTemplate(
                         dataTemplateXls:String,
                         istream:FileInputStream,
                         ignoreSheet:List[String]
                       )={
    val locations = getExcelLocation(dataTemplateXls)

    val f = istream
    val workbook = WorkbookFactory.create(f)

    val result = (for (index <- 0 until workbook.getNumberOfSheets)  yield {
      val sheet = workbook.getSheetAt(index)

      if(ignoreSheet.contains(sheet.getSheetName)) {
        null
      } else {

        var results = scala.collection.immutable.Map[String,Any]()


        locations.map {
          x =>
            //retrieve ${}'s list
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
            val regEx = ("(?s)" + x._4.foldLeft(if (x._3==null){""}else{x._3.toString}) {
              (acc, xx) =>
                val xxx = xx.replace("$", "\\$")
                  .replace("{", "\\{")
                  .replace("}", "\\}")
                acc.replaceFirst(xxx, "(.+)")
            })
              .r

            val matchValue = if(target==null)
            {
              ""
            }
            else {
              target.getCellTypeEnum match {
                case CellType.STRING =>
                  target.getStringCellValue
                case CellType.NUMERIC =>
                  target.getNumericCellValue.toString
                case CellType.BLANK =>
                  ""
                case CellType.FORMULA =>
                  target.getStringCellValue
                case _ =>
                  target.getStringCellValue
              }
            }

            val all = regEx.findFirstMatchIn(matchValue)

            if (all.size>0) {
              (for (i <- 0 until all.get.groupCount) yield {
                results += (x._4(i).replaceAll("^\\$\\{", "").replaceAll("\\}$", "") -> all.get.group(i + 1))
              })
            }
            else {
              (x._4.map {
                xx =>
                  results += (xx.replaceAll("^\\$\\{", "").replaceAll("\\}$", "") -> "")
              })
            }
        }
        results
      }
    }).filter(_!=null).toList

    workbook.close()
    result
  }


  def setData(dataTempleteXls:String, outXlsPath:String, locationData:List[(String,String,Any)])={

    val f = new File(dataTempleteXls)
    val workbook = WorkbookFactory.create(f)

    locationData.foreach{
      x =>
        val sheet = workbook.getSheet(x._1)
        val ref = new CellReference(x._2)
        val row = sheet.getRow(ref.getRow)
        val target = row.getCell(ref.getCol)
        x._3 match {
          case xx:String =>
            target.setCellValue(xx)
          case xx:Int =>
            target.setCellValue(xx)
          case _ =>
            target.setCellValue(x._3.toString)
        }
    }

    val w = new File(outXlsPath)
    val out = new FileOutputStream(w)

    workbook.write(out)
    workbook.close()

  }

}
