package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

case class Data(name:String, address:String)

@RunWith(classOf[JUnitRunner])
class ETest extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()

  "E" should {

    "&" in {
      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      import com.axtstar.asta4e.converter.E._

      val target = "Sheet1" -> (
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
            "date" -> dateFormat.parse("2018/7/8") &
            "formula" -> "=B3" &
            "bool" -> false &
            "time" -> timeFormat.parse("23:32:42") &
            "userDate" -> dateFormatFull.parse("2018/11/24 18:52:56")
          )

      target.size must be_==(2)

      target(0)._1 must be_==("Sheet1")

      target(0)._2("numeric") must be_==(1001)
      target(0)._2("string") must be_==("1000")
      target(0)._2("date") must be_==(dateFormat.parse("2018/7/7"))
      target(0)._2("formula") must be_==("=B2")
      target(0)._2("bool") must be_==(true)
      target(0)._2("time") must be_==(timeFormat.parse("23:32:41"))
      target(0)._2("userDate") must be_==(dateFormatFull.parse("2018/11/23 18:52:56"))

      target(1)._1 must be_==("Sheet2")

      target(1)._2("numeric") must be_==(1002)
      target(1)._2("string") must be_==("1001")
      target(1)._2("date") must be_==(dateFormat.parse("2018/7/8"))
      target(1)._2("formula") must be_==("=B3")
      target(1)._2("bool") must be_==(false)
      target(1)._2("time") must be_==(timeFormat.parse("23:32:42"))
      target(1)._2("userDate") must be_==(dateFormatFull.parse("2018/11/24 18:52:56"))

    }

  }
}
