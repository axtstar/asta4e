package com.axtstar.asta4e

import java.io.File

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import com.axtstar.asta4e.converter.E._

case class Data(name:String, address:String)

@RunWith(classOf[JUnitRunner])
class ExcelMapperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "ExcelMapper" should {
    "getExcelLocation" in {

      val target = ExcelMapper.getExcelLocation(s"${currentDir}/src/test/resources/excel/bind_template1.xlsx")
      target.size must be_==(240)

    }

    "getDataAsTemplate" in {

      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample1.xlsx")

      target(0).size must be_==(240)
      target(0)("A1") must be_==("A1")
      target(0)("A2") must be_==("A2")

      target(0)("B3") must be_==("B3")
      target(0)("C5") must be_==("C5")
    }

    "setDataAsTemplate to getDataAsTemplate" in {

      val target = ExcelMapper.setDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample1.xlsx",
      s"${currentDir}/target/output1.xlsx",
       "A1" ->
         """t
           |e
           |s
           |t
           |1""".stripMargin &
        "A2" -> null &
        "A3" -> "test3" &
        "A4" -> 1
      )

      val result = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/target/output1.xlsx")

      result(0)("A1") must be_==(
          """t
            |e
            |s
            |t
            |1""".stripMargin
      )
      result(0)("A2") must be_==("")
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }

    "setData to getDataAsTemplate" in {

      val target = ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample1.xlsx",
        s"${currentDir}/target/output3.xlsx",
        "Sheet1" -> ("A1" ->
          """t
            |e
            |s
            |t
            |1""".stripMargin &
          "A2" -> null &
          "A3" -> "test3" &
          "A4" -> 1)
      )

      val result = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/target/output3.xlsx")

      result(0)("A1") must be_==(
        """t
          |e
          |s
          |t
          |1""".stripMargin
      )
      result(0)("A2") must be_==("")
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }

  }
}
