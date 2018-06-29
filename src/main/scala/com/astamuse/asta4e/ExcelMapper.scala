package com.astamuse.asta4e

import java.io.{File, FileInputStream, FileOutputStream}

import com.astamuse.asta4e.utils.Helper
import org.apache.poi.ss.usermodel.{Cell, WorkbookFactory}
import org.apache.poi.ss.util.{CellAddress, CellReference}
import shapeless._
import shapeless.labelled.{FieldType, field}

object ExcelMapper {

  val allReplaceBrace = "\\$\\{([^\\}]*)\\}".r

  /**
    * xlsファイルから${}の文字列を探し出してそのロケーションと文字列のペアを返す
    * @param xlsPath
    *
    */
  def getExcelLocation(xlsPath:String) = {

    val f = new FileInputStream(xlsPath)
    val workbook = WorkbookFactory.create(f)

    val results = (for (i <- 0 to workbook.getNumberOfSheets - 1) yield {
      val sheet = workbook.getSheetAt(i)
      (for (rowID <- 0 to sheet.getLastRowNum) yield {
        val row = sheet.getRow(rowID)
        (for (columnID <- 0 to row.getLastCellNum) yield {
          val cell = row.getCell(columnID)
          cell match {
            case null =>
              (null, "", "", Nil)
            case x:Cell =>
              val all = allReplaceBrace.findAllIn(x.toString)
              var result:List[String] = Nil
              while(all.hasNext){
                val d = all.next()
                result = d :: result
              }
              if (result==Nil){
                (null, "", "", Nil)
              }else {
                //シート名 -> セルアドレス -> バインダの値 -> List(バインダ)
                (sheet.getSheetName, cell.getAddress, cell, result.reverse)
              }
          }
        })
          .filter(_._1!=null) //nullを省く
          .toList
      })
        .filter(_.size > 0)
        .toList
    }).flatten.flatten.toList

    workbook.close()

    results
  }

  /**
    * データバインドをExcelに出力
    * @param dataTemplateXls ${}のあるひな形ファイルテンプレート
    * @param outTemplate ひな形ファイル（出力用フォーマット）
    * @param outXlsPath 出力先のExcel
    * @param locationDataArray
    */
  def setDataAsTemplate(
                         dataTemplateXls:String,
                         outTemplate:String,
                         outXlsPath:String,
                         locationDataArray:Map[String,Any] *
                       )={
    val locations = getExcelLocation(dataTemplateXls)

    val f = new FileInputStream(outTemplate)
    val workbook = WorkbookFactory.create(f)

    var sheetIndex = 0
    locationDataArray.map {
      locationData =>

        //sheetの決定
        val sheet = if (sheetIndex==0) {
          workbook.getSheet(locations.head._1)
        }
        else{
          //シートをコピー
          val sheet = workbook.cloneSheet(0)
          sheet
        }

        locations.foreach {
          x =>
            //${}のList取得
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


    /**
    * Excelからデータバインドを取得
    * @param dataTemplateXls ${}のあるひな形ファイルテンプレート
    * @param inputXlsPath ひな形ファイル（出力用フォーマット）
    */
  def getDataAsTemplate(
                         dataTemplateXls:String,
                         inputXlsPath:String,
                         ignoreSheet:List[String]=List("設定")
                       )={
    val locations = getExcelLocation(dataTemplateXls)

    val f = new FileInputStream(inputXlsPath)
    val workbook = WorkbookFactory.create(f)

    val result = (for (index <- 0 until workbook.getNumberOfSheets)  yield {
      val sheet = workbook.getSheetAt(index)

      if(ignoreSheet.contains(sheet.getSheetName)) {
        null
      } else {

        var results = scala.collection.immutable.Map[String,Any]()


        locations.map {
          x =>
            //${}のList取得
            val ref = new CellReference(x._2.toString)
            val row = sheet.getRow(ref.getRow)
            val target = row.getCell(ref.getCol)

            val regEx = ("(?s)" + x._4.foldLeft(if (x._3==null){""}else{x._3.toString}) {
              (acc, xx) =>
                val xxx = xx.replace("$", "\\$")
                  .replace("{", "\\{")
                  .replace("}", "\\}")
                acc.replaceFirst(xxx, "(.+)")
            })
              .r

            val all = regEx.findFirstMatchIn(target.getStringCellValue)

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
