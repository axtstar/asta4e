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

    }

  }

}
