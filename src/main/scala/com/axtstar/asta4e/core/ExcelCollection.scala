package com.axtstar.asta4e.core

import java.io.FileInputStream

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference

object ExcelCollection {

  case class GetData(locationMap: List[Location], dataXlsPath: String) {

    import com.axtstar.asta4e.core.ExcelBasic._

    def map[B](f: Map[String, Any] => B) = {
      val iStream = new FileInputStream(dataXlsPath)
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
          f(result.toMap)
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
  }

  case class GetDataDown(locationMap: List[Location], dataXlsPath: String,ignoreSheet:List[String]) {

    import com.axtstar.asta4e.core.ExcelBasic._

    def map[B](f: Map[String, Any] => B) = {
      val iStream = new FileInputStream(dataXlsPath)
      val workbook = WorkbookFactory.create(iStream)

      try {

        (for (index <- 0 until workbook.getNumberOfSheets) yield {
          val sheet = workbook.getSheetAt(index)

          if (ignoreSheet.contains(sheet.getSheetName)) {
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

}
