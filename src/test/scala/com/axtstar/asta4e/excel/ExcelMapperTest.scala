package com.axtstar.asta4e.excel

import java.io.{File, FileInputStream, FileOutputStream}
import java.text.SimpleDateFormat

import com.axtstar.asta4e.converter.{CC, MapHelper}
import com.axtstar.asta4e.test_class.{Etc7Option, VariousCell}
import com.axtstar.asta4e.ExcelMapper
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExcelMapperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "ExcelMapper" should {

    "primitive" should {

      val target = ExcelMapper
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
        ._getData(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_excel_mapper.xlsx"))

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
          val target = CC.By(d).toMap
          target must be_==(Map( "name" -> "axtstar", "address" -> "Tokyo, Japan" ))
        }
      }

      "map to A" in {
        val target = ExcelMapper
            .withLocation(s"${currentDir}/src/test/resources/excel/bind_template6.xlsx")
          ._getDataDown(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_sample6.xlsx"))

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

        val result3 = CC.By(result).toMap

        val ff_1 = java.io.File.createTempFile(s"${currentDir}/target/","output6_1.csv")

        //case class
        ExcelMapper.by[List[Etc7Option]]
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_template6.xlsx")
          .withLayoutXls(s"${currentDir}/src/test/resources/excel/output_template6.xlsx")
          .withOutXls(ff_1.getAbsolutePath)
          ._setDataDown("Sheet1" -> IndexedSeq(CC.By(result).toMap, CC.By(result2).toMap))


        val ff_2 = java.io.File.createTempFile(s"${currentDir}/target/","output6_1.csv")

        //Map
        ExcelMapper.by[String]
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_template6.xlsx")
          .withLayoutXls(s"${currentDir}/src/test/resources/excel/output_template6.xlsx")
          .withOutXls(ff_2.getAbsolutePath)
          ._setDataDown(target: _*)

        val result4 = ExcelMapper.by[Etc7Option]
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_template6.xlsx")
          ._getDataDown(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_sample6.xlsx"))

        result4.size must be_==(1) // 1 sheet

        "" must_== ("")
      }

      "A to B" in {
        val data = ExcelMapper.by[VariousCell]
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
          .getCC(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_excel_mapper.xlsx"))

        val ff_3 = java.io.File.createTempFile(s"${currentDir}/target/","output6_3.csv")

        ExcelMapper.by[VariousCell]
          .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
          .withIgnoreSheets(List())
          .withLayoutXls(new FileInputStream(s"${currentDir}/src/test/resources/excel/output_template6.xlsx"))
          .withOutStream(new FileOutputStream(ff_3.getAbsolutePath))
          .setCC(data)

        "" must_== ("")
      }

    }

  }
}
