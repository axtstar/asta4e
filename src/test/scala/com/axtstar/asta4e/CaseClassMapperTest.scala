package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import com.axtstar.asta4e.core._
import com.axtstar.asta4e.test_class._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class CaseClassMapperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  val map = Map(
    "name" -> "axtstar",
    "address" -> "Tokyo, Japan"
  )

  "ToMap" should {
    "toMap" in {
      val d = Data("axtstar","Tokyo, Japan")
      val target = ExcelMapper.By(d).toMap
      target must be_==(Map( "name" -> "axtstar", "address" -> "Tokyo, Japan" ))
    }
  }

  "ExcelMapper" should {
    "from[Map]" in {
      val result = ExcelHelper.to[Data].from(map)

      result.name must be_==("axtstar")
      result.address must be_==("Tokyo, Japan")

    }

    "from[get]" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template2.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample2.xlsx",
        List()
      )

//      val result = ExcelMapper.apply[Data].from(target(0))
      val result = ExcelHelper.to[Data].from(target.head._2)

      result.name must be_==("axtstar")
      result.address must be_==("Tokyo, Japan")

    }

    "Data23" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        List()
      )

      val result = ExcelHelper.to[Data23].from(target.head._2)

      result.A1 must be_==("A1")
      result.A2 must be_==("A2")

    }

    "Data28" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        List()
      )

      val result = ExcelHelper.to[Data28].from(target.head._2)

      result.A1 must be_==("A1")
      result.A2 must be_==("A2")

    }

    "Data29" in {
      val result = ExcelMapper.by[Data29].getDataAsAny(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        List()
      )

      result.head._2.get.A1 must be_==("A1")
      result.head._2.get.A2 must be_==("A2")

      ExcelMapper.by[Data29].setData4cc(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/target/output3_1.xlsx",
        result
      )

      "" must be_==("")

    }

    "Data64" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        List()
      )

      val result = ExcelHelper.to[Data64].from(target.head._2)

      result.A1 must be_==("A1")
      result.A2 must be_==("A2")

    }

    "Last10" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        List()
      )

      val result = ExcelHelper.to[Last10].from(target.head._2)

      result.B16 must be_==("B16")
      result.D16 must be_==("D16")

    }

    "Etc7Option" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
        List("設定")
      )

      val result = ExcelHelper.to[Etc7Option].fromAsOption(target.head._2)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result.get.numeric must be_==(Some(111))
      result.get.string must be_==(Some("111"))

      result.get.date must be_==(Some(dateFormat.parse("1970/01/01")))
      result.get.formula must be_==(Some("111"))
      result.get.bool must be_==(Some(true))
      result.get.time must be_==(Some(dateFormatFull.parse("1899/12/31 17:25:47")))
      result.get.userDate must be_==(Some(dateFormatFull.parse("2018/7/2 22:35:54")))

    }

    "Etc7Option(alt)" in {
      val result = ExcelMapper.by[Etc7Option].getDataAsOption(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
        List("設定")
      )

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result(0)._2.get.numeric must be_==(Some(111))
      result(0)._2.get.string must be_==(Some("111"))

      result(0)._2.get.date must be_==(Some(dateFormat.parse("1970/01/01")))
      result(0)._2.get.formula must be_==(Some("111"))
      result(0)._2.get.bool must be_==(Some(true))
      result(0)._2.get.time must be_==(Some(dateFormatFull.parse("1899/12/31 17:25:47")))
      result(0)._2.get.userDate must be_==(Some(dateFormatFull.parse("2018/7/2 22:35:54")))

      ExcelMapper.by[Etc7Option].setData4cc(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx",
        s"${currentDir}/target/output4_4.xlsx",
        result
      )

      "" must be_==("")

    }

    "Etc7Option null" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4-1.xlsx",
        List("設定")
      )

      val result = ExcelHelper.to[Etc7Option].fromAsOption(target.head._2)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result.get.numeric must be_==(Some(2))
      result.get.string must be_==(None)

      result.get.date must be_==(None)
      result.get.formula must be_==(None)
      result.get.bool must be_==(None)
      result.get.time must be_==(None)
      result.get.userDate must be_==(None)

      val result2 = ExcelMapper.By(result.get).toMap

      result2("numeric") must be_==(2)
      result2("numeric") must be_==(2)

    }

    "Etc7 null" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4-1.xlsx",
        List("設定")
      )

      // not Option case class return naked primitive value
      // if not determine from excel data, asta4e return default value or something
      val result = ExcelHelper.to[Etc7].from(target.head._2)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result.numeric must be_==(2.0D)
      result.string must be_==("")

      result.date must be_==(new java.util.Date(Long.MinValue))
      result.formula must be_==("")
      result.bool must be_==(false)
      result.time must be_==(new java.util.Date(Long.MinValue))
      result.userDate must be_==(new java.util.Date(Long.MinValue))

      val result2 = ExcelMapper.By(result).toMap

      result2("numeric") must be_==(2)
      result2("numeric") must be_==(2)

    }


    "GetDataDown" in {
      val target = ExcelMapper.getDataDown(
        s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample6.xlsx",
        List("設定")
      )

      val result = ExcelHelper.to[Etc7Option].fromAsOption(target.head._2.head)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result.get.numeric must be_==(Some(1.0))
      result.get.string must be_==(Some("Test"))

      result.get.date must be_==(Some(dateFormat.parse("2020/01/02")))
      result.get.formula must be_==(Some("Str"))
      result.get.bool must be_==(Some(true))
      result.get.time must be_==(Some(dateFormat.parse("2020/01/02")))
      result.get.userDate must be_==(Some(dateFormat.parse("2020/01/02")))

      val result2 = ExcelHelper.to[Etc7Option].fromAsOption(target.head._2.tail.head)

      result2.get.numeric must be_==(Some(2.0))
      result2.get.string must be_==(Some("漢字"))

      result2.get.date must be_==(Some(dateFormat.parse("2020/01/02")))
      result2.get.formula must be_==(Some("Rts"))
      result2.get.bool must be_==(Some(false))
      result2.get.time must be_==(Some(dateFormat.parse("2020/01/02")))
      result2.get.userDate must be_==(Some(dateFormat.parse("2020/01/02")))

      val result3 = ExcelMapper.By(result.get).toMap

      //case class
      ExcelMapper.by[List[Etc7Option]].setDataDown(
        s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template6.xlsx",
        s"${currentDir}/target/output6_1.xlsx",
        "Sheet1" -> IndexedSeq(ExcelMapper.By(result.get).toMap, ExcelMapper.By(result2.get).toMap)
      )

      //Map
      ExcelMapper.setDataDown(
        s"${currentDir}/src/test/resources/excel/bind_template6.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template6.xlsx",
        s"${currentDir}/target/output6_2.xlsx",
        target:_*
      )

      "" must_==("")
    }



  }
}
