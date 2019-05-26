package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.axtstar.asta4e.core.ExcelBasic
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExcelBasicTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()


  "ExcelBasic" should {
    "location" in {
      val target = ExcelBasic.getExcelLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
      target.size must be_==(16)

      target.map {
        x =>
          x.name match {
            case "${string}" =>
              x.positionX must be_==(0)
            case "${float}" =>
              x.positionY must be_==(1)
            case _ =>
              "" must be_==("")
          }
      }
    }


  }


}
