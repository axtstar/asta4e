package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.axtstar.asta4e.converter.MapHelper
import com.axtstar.asta4e.core.{ExcelBasic, ExcelCollection}
import com.axtstar.asta4e.test_class.Etc7Option
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

    "ExcelCollection" should {
      "GetData" in {
        val locationMap = ExcelBasic.getExcelLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
        val target = ExcelCollection.GetData(locationMap,s"${currentDir}/src/test/resources/excel/read_excel_mapper.xlsx").map {
          x =>
            x
        }

        target.size must be_==(1)
        target(0)("string") must be_==("STRING")
        target(0)("int") must be_==(11101)


      }

      "GetDataDown" in {
        val _target = ExcelCollection.GetDataDown(
          ExcelBasic.getExcelLocation(s"${currentDir}/src/test/resources/excel/bind_template6.xlsx"),
          s"${currentDir}/src/test/resources/excel/read_sample6.xlsx",
          List()
        )

        val target = _target.map {
          x =>
            x
        }

        val result = MapHelper.to[Etc7Option].from(target.head._2.head.asInstanceOf[Map[String, Any]])

        val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
        val timeFormat = new SimpleDateFormat("HH:mm:ss")

        val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        result.numeric must be_==(Some(1.0))
        result.string must be_==(Some("Test"))

        result.date must be_==(Some(dateFormat.parse("2020/01/02")))
        result.formula must be_==(Some("Str"))
        result.bool must be_==(Some(true))
        result.time must be_==(Some(dateFormat.parse("2020/01/02")))
        result.userDate must be_==(Some(dateFormat.parse("2020/01/02")))

        val result2 = MapHelper.to[Etc7Option].from(target.head._2(1).asInstanceOf[Map[String, Any]])

        result2.numeric must be_==(Some(2.0))
        result2.string must be_==(Some("漢字"))

        result2.date must be_==(Some(dateFormat.parse("2020/01/02")))
        result2.formula must be_==(Some("Rts"))
        result2.bool must be_==(Some(false))
        result2.time must be_==(Some(dateFormat.parse("2020/01/02")))
        result2.userDate must be_==(Some(dateFormat.parse("2020/01/02")))


      }

    }

  }

}
