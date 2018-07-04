package com.axtstar.asta4e.utils

import java.io.File

import com.axtstar.asta4e.ExcelMapper
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner])
class HelperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "ToMapOps" should {
    "toMap" in {
      val d = Data("axtstar","Tokyo, Japan")
      val target = Helper.ToMapOps(d).toMap
      target must be_==(Map( "name" -> "axtstar", "address" -> "Tokyo, Japan" ))
    }
  }

  "ToMapOps" should {
    "toExcel" in {
      val d = Data("axtstar","Tokyo, Japan")
      Helper.ToMapOps(d).toExcel(
        s"${currentDir}/src/test/resources/excel/bind_template2.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample2.xlsx",
        s"${currentDir}/target/output2.xlsx"
      )

      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template2.xlsx",
        s"${currentDir}/target/output2.xlsx"
      )

      val result = Helper.to[Data].from(target(0))

      result.get.name must be_==("axtstar")
      result.get.address must be_==("Tokyo, Japan")
    }
  }

  "Helper" should {
    "from[Map]" in {
      val map = Map(
        "name" -> "axtstar",
        "address" -> "Tokyo, Japan"
      )
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

  }
}
