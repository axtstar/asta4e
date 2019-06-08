package com.axtstar.asta4e.excel

import java.io.{File, FileInputStream}

import com.axtstar.asta4e.ExcelMapper
import com.axtstar.asta4e.test_class._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class XSSFCellTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "XSSFCell" should {
    "Get" in {
      val target = ExcelMapper.by[ExcelCell]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_cell.xlsx")
        ._getData(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_excel_cell.xlsx"))

      target.size must be_==(1)
      target(0)._2("A1") must be_==("A2")
      target(0)._2("B1") must be_==("B2")
      target(0)._2("C1_1") must be_==("1")
      target(0)._2("C1_2") must be_==("2")

    }

    "Set" in {
      val map = "Sheet0" -> Map(
        "A1" -> "A1",
        "B1" -> "B1",
        "C1_1" -> "3",
        "C1_2" -> "4"
      )

      ExcelMapper.by[ExcelCell]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_cell.xlsx")
        .withOutXls(s"${currentDir}/target/out_bind_excel_cell.xlsx")
        ._setData(map)

      val target = ExcelMapper.by[ExcelCell]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_cell.xlsx")
        ._getData(new FileInputStream(s"${currentDir}/target/out_bind_excel_cell.xlsx"))

      target.size must be_==(1)
      target(0)._2("A1") must be_==("A1")
      target(0)._2("B1") must be_==("B1")
      target(0)._2("C1_1") must be_==("3")
      target(0)._2("C1_2") must be_==("4")

    }


  }

}
