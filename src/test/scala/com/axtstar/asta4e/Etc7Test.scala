package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import com.axtstar.asta4e.core.ExcelHelper
import com.axtstar.asta4e.test_class.{Etc7, Etc7Comp}

import com.axtstar.asta4e.core._
import com.axtstar.asta4e.test_class._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Etc7Test extends Specification {

  val currentDir = new File(".").getAbsoluteFile().getParent()


  "Etc7" in {
    val target = ExcelMapper.getData(
      s"${currentDir}/src/test/resources/excel/bind_template4.xlsx",
      s"${currentDir}/src/test/resources/excel/read_sample4.xlsx",
      List("設定")
    )

    val result = ExcelHelper.to[Etc7].from(target.head._2)

    val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
    val timeFormat = new SimpleDateFormat("HH:mm:ss")

    val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    result.numeric must be_==(111)
    result.string must be_==("111")

    result.date must be_==(dateFormat.parse("1970/01/01"))
    result.formula must be_==("111")
    result.bool must be_==(true)
    result.time must be_==(dateFormatFull.parse("1899/12/31 17:25:47"))
    result.userDate must be_==(dateFormatFull.parse("2018/7/2 22:35:54"))


    val result2 = ExcelHelper.to[Etc7Comp].from(target.head._2)
    result2.bool must_==(true)


  }


}
