package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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

    "getData with format" in {
      val target = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx")

      target(0)("numeric") must be_==(111)
      target(0)("string") must be_==("111")
      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      target(0)("date") must be_==(dateFormat.parse("1970/1/1"))
      target(0)("formula") must be_==("111")
      target(0)("bool") must be_==(true)

      val timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
      //TODO : Excel return 1899
      target(0)("time") must be_==(timeFormat.parse("1899/12/31 17:25:47"))

      target(0)("userDate") must be_==(timeFormat.parse("2018/07/02 22:35:54"))
  }

    "setDataAsTemplate to getDataAsTemplate" in {

      val target = ExcelMapper.setDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template1.xlsx",
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
      result(0)("A2") must be_==(null)
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }

    "setData to getDataAsTemplate" in {

      val target = ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template1.xlsx",
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
      result(0)("A2") must be_==(null)
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }

    "setData 2Sheets to getDataAsTemplate" in {

      val target = ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template1.xlsx",
        s"${currentDir}/target/output4.xlsx",
        "Sheet1" -> ("A1" ->
          """t
            |e
            |s
            |t
            |1""".stripMargin &
          "A2" -> null &
          "A3" -> "test3" &
          "A4" -> 1) &
          "Sheet2" -> ("A1" ->
            """t
              |e
              |s
              |t
              |1""".stripMargin &
            "A2" -> null &
            "A3" -> "test3" &
            "A4" -> 1) :_*
      )

      val result = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/target/output4.xlsx")

      result.size must be_==(2)

      result(0)("A1") must be_==(
        """t
          |e
          |s
          |t
          |1""".stripMargin
      )
      result(0)("A2") must be_==(null)
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }

    "setData 3Sheets to getDataAsTemplate" in {

      val target = ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template1.xlsx",
        s"${currentDir}/target/output5.xlsx",
        "Sheet1" -> ("A1" ->
          """t
            |e
            |s
            |t
            |1""".stripMargin &
          "A2" -> null &
          "A3" -> "test3" &
          "A4" -> 1) &
          "Sheet2" -> ("A1" ->
            """t
              |e
              |s
              |t
              |1""".stripMargin &
            "A2" -> null &
            "A3" -> "test3" &
            "A4" -> 1)  &
          "Sheet3" -> ("A1" ->
            """t
              |e
              |s
              |t
              |1""".stripMargin &
            "A2" -> null &
            "A3" -> "test3" &
            "A4" -> 1) :_*
      )

      val result = ExcelMapper.getDataAsTemplate(
        s"${currentDir}/src/test/resources/excel/bind_template1.xlsx",
        s"${currentDir}/target/output5.xlsx")

      result.size must be_==(3)

      result(0)("A1") must be_==(
        """t
          |e
          |s
          |t
          |1""".stripMargin
      )
      result(0)("A2") must be_==(null)
      result(0)("A3") must be_==("test3")
      result(0)("A4") must be_==("1")

    }


  }
}
