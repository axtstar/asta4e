package com.axtstar.asta4e.utils

import java.io.File
import java.text.SimpleDateFormat

import com.axtstar.asta4e.ExcelMapper
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class HelperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  val map = Map(
    "name" -> "axtstar",
    "address" -> "Tokyo, Japan"
  )

  "ToMap" should {
    "toMap" in {
      val d = Data("axtstar","Tokyo, Japan")
      val target = Helper.ToMapOps(d).toMap
      target must be_==(Map( "name" -> "axtstar", "address" -> "Tokyo, Japan" ))
    }
  }

  "Helper" should {
    "from[Map]" in {
      val result:Option[Data] = Helper.to[Data].from(map)

      result.get.name must be_==("axtstar")
      result.get.address must be_==("Tokyo, Japan")

    }

    "from[get]" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template2.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample2.xlsx")

      val result = Helper.to[Data].from(target(0))

      result.get.name must be_==("axtstar")
      result.get.address must be_==("Tokyo, Japan")

    }

    "Data23" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx")

      val result = Helper.to[Data23].from(target(0))

      result.get.A1 must be_==("A1")
      result.get.A2 must be_==("A2")

    }

    "Data28" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx")

      val result = Helper.to[Data28].from(target(0))

      result.get.A1 must be_==("A1")
      result.get.A2 must be_==("A2")

    }

    "Data29" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx")

      val result = Helper.to[Data29].from(target(0))

      result.get.A1 must be_==("A1")
      result.get.A2 must be_==("A2")

    }

    "Data64" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx")

      val result = Helper.to[Data64].from(target(0))

      result.get.A1 must be_==("A1")
      result.get.A2 must be_==("A2")

    }

    "Last10" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template3.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample3.xlsx")

      val result = Helper.to[Last10].from(target(0))

      result.get.B16 must be_==("B16")
      result.get.D16 must be_==("D16")

    }

    "Etc7" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
        List("設定")
      )

      val result = Helper.to[Etc7].from(target.head._2)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      result.get.numeric must be_==(111)
      result.get.string must be_==("111")

      result.get.date must be_==(dateFormat.parse("1970/01/01"))
      result.get.formula must be_==("111")
      result.get.bool must be_==(true)
      result.get.time must be_==(dateFormatFull.parse("1899/12/31 17:25:47"))
      result.get.userDate must be_==(dateFormatFull.parse("2018/7/2 22:35:54"))

    }

    "Etc7Option" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
        List("設定")
      )

      val result = Helper.to[Etc7Option].fromOp(target.head._2)

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

    "Etc7Option null" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4-1.xlsx",
        List("設定")
      )

      val result = Helper.to[Etc7Option].fromOp(target.head._2)

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

      val result2 = Helper.ToMapOps(result.get).toMap

      result2("numeric") must be_==(2)
      result2("numeric") must be_==(2)

    }


  }
}
