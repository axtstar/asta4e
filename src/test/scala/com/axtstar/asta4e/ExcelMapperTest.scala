package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import com.axtstar.asta4e.converter.E._
import com.axtstar.asta4e.test_class._

case class Data(name:String, address:String)

@RunWith(classOf[JUnitRunner])
class ExcelMapperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "ExcelMapper" should {
    "setData -> getData" in {

      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx",
        List()
      )

      target.head._2.size must be_==(16384)
      target.head._2("A1") must be_==("A1")
      target.head._2("B1") must be_==("B1")

      target.head._2("C1") must be_==("C1")
      target.head._2("D1") must be_==("D1")
    }

    "getDataAsNumeric" in {

      val target = ExcelMapper.by[Etc3Option].getDataAsOption(
        s"${currentDir}/src/test/resources/excel/bind_template5.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample5.xlsx",
        List()
      )

      target(0)._2.get.getNum1 must be_==(111D)
      target(0)._2.get.getNum2 must be_==(123D)
      target(0)._2.get.getNum3 must be_==(124D)
    }


    "several format" in {
      val target = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
        List()
      )

      target.head._2("numeric") must be_==(111)
      target.head._2("string") must be_==("111")
      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      target.head._2("date") must be_==(dateFormat.parse("1970/1/1"))
      target.head._2("formula") must be_==("111")
      target.head._2("bool") must be_==(true)

      val timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
      //TODO : Excel return 1900/01/00 HH:mm:ss
      target.head._2("time") must be_==(timeFormat.parse("1899/12/31 17:25:47"))

      target.head._2("userDate") must be_==(timeFormat.parse("2018/07/02 22:35:54"))
  }

    "setData with format to getData with format" in {
      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      val target = ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/src/test/resources/excel/output_template4.xlsx",
        s"${currentDir}/target/output4_2.xlsx",
        "Sheet1" -> (
          "numeric" -> 1001 &
          "string" -> "1000" &
          "date" -> dateFormat.parse("2018/7/7") &
          "formula" -> "=B2" &
          "bool" -> true &
          "time" -> timeFormat.parse("23:32:41") &
          "userDate" -> dateFormatFull.parse("2018/11/23 18:52:56")
        ) &
        "Sheet2" -> (
          "numeric" -> 1002 &
            "string" -> "1001" &
            "date" -> dateFormat.parse("2018/7/7") &
            "formula" -> "=B2" &
            "bool" -> true &
            "time" -> timeFormat.parse("23:32:41") &
            "userDate" -> dateFormatFull.parse("2018/11/23 18:52:56")
          ) :_*
      )

      val result = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/target/output4_2.xlsx",
        List()
      )

      result(0)._2("numeric") must be_==(1001)
      result(0)._2("string") must be_==("1000")
      result(0)._2("date") must be_==(dateFormat.parse("2018/7/7"))
      result(0)._2("formula") must be_==("=B2")
      result(0)._2("bool") must be_==(true)
      result(0)._2("time") must be_==(timeFormat.parse("23:32:41"))
      result(0)._2("userDate") must be_==(dateFormatFull.parse("2018/11/23 18:52:56"))

      ExcelMapper.setData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/target/output4_2.xlsx",
        s"${currentDir}/target/output4_3.xlsx",
        result :_*
      )

      val result2 = ExcelMapper.getData(
        s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
        s"${currentDir}/target/output4_3.xlsx",
        List()
      )

      result2(0)._2("numeric") must be_==(1001)
      result2(0)._2("string") must be_==("1000")
      result2(0)._2("date") must be_==(dateFormat.parse("2018/7/7"))
      result2(0)._2("formula") must be_==("=B2")
      result2(0)._2("bool") must be_==(true)
      result2(0)._2("time") must be_==(timeFormat.parse("23:32:41"))
      result2(0)._2("userDate") must be_==(dateFormatFull.parse("2018/11/23 18:52:56"))
    }

  }
}
