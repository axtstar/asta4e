package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import com.axtstar.asta4e.converter.MapHelper
import com.axtstar.asta4e.test_class.Etc7Option
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class ExcelMapperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "ExcelMapper" should {

    "primitive" should {

      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx",
        s"${currentDir}/src/test/resources/excel/read_excel_mapper.xlsx",
        List()
      )

      "sheets size" in {
        target.size must be_==(1)
      }

      "column sizes" in {
        target(0)._2.size must be_==(16)
      }

      "string" in {
        target.head._2("string") must be_==("STRING")
      }

      "int" in {
        target.head._2("int") must be_==(11101)
      }

      "long" in {
        target.head._2("long") must be_==(11101)
      }

      "date" in {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
        target.head._2("date") must be_==(dateFormat.parse("2019/6/1"))

      }

      "boolean" in {
        target.head._2("boolean") must be_==(true)
      }


      "float" in {
        target.head._2("float") must be_==(1110.1D)
      }

      "double" in {
        target.head._2("double") must be_==(1.000009D)
      }

      "formula" in {
        target.head._2("formula") must be_==(5.0)
      }

      "stringOpt" in {
        target.head._2("stringOpt") must be_==("STRING1")
      }


    }

    "top level function" should {

      "ToMap" should {
        "toMap" in {
          val d = Data("axtstar","Tokyo, Japan")
          val target = MapHelper.By(d).toMap
          target must be_==(Map( "name" -> "axtstar", "address" -> "Tokyo, Japan" ))
        }
      }

      "map to A" in {
        val target = ExcelMapper.getDataDown(
          s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
          s"${currentDir}/src/test/resources/excel/read_sample6.xlsx",
          List()
        )

        val result = MapHelper.to[Etc7Option].from(target.head._2.head)

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

        val result2 = MapHelper.to[Etc7Option].from(target.head._2(1))

        result2.numeric must be_==(Some(2.0))
        result2.string must be_==(Some("漢字"))

        result2.date must be_==(Some(dateFormat.parse("2020/01/02")))
        result2.formula must be_==(Some("Rts"))
        result2.bool must be_==(Some(false))
        result2.time must be_==(Some(dateFormat.parse("2020/01/02")))
        result2.userDate must be_==(Some(dateFormat.parse("2020/01/02")))

        val result3 = MapHelper.By(result).toMap

        //case class
        ExcelMapper.by[List[Etc7Option]].setDataDown(
          s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
          s"${currentDir}/src/test/resources/excel/output_template6.xlsx",
          s"${currentDir}/target/output6_1.xlsx",
          "Sheet1" -> IndexedSeq(MapHelper.By(result).toMap, MapHelper.By(result2).toMap)
        )

        //Map
        ExcelMapper.setDataDown(
          s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
          s"${currentDir}/src/test/resources/excel/output_template6.xlsx",
          s"${currentDir}/target/output6_2.xlsx",
          target: _*
        )

        val result4 = ExcelMapper.by[Etc7Option].getDataDown(
          s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
          s"${currentDir}/src/test/resources/excel/read_sample6.xlsx",
          List()
        )

        result4.size must be_==(1) // 1 sheet

        "" must_== ("")
      }
    }

  }
}
