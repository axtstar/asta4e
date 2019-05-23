package com.axtstar.asta4e

import java.io.File
import java.text.SimpleDateFormat

import com.axtstar.asta4e._
import com.axtstar.asta4e.test_class.{Data_HOLIZONTAL, Etc7, Etc7Comp}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HorizontalTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "Horizontal" should {
    "Get" in {
      val target = ExcelMapper.by[Data_HOLIZONTAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx",
        List()
      )
      target.size must be_==(1)

    }
  }

}
