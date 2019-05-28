package com.axtstar.asta4e.csv

import java.io.{File, FileInputStream}

import com.axtstar.asta4e.{CsvMapper, ExcelMapper}
import com.axtstar.asta4e.test_class._
import com.axtstar.asta4e.test_location.{CSV_Data, Location_4_CSV}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CsvTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "CSV" should {
    "Get 0 row" in {
      val target = CsvMapper.by[CSV_Data]
        .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_0)
        .getCC(
        new FileInputStream(s"${currentDir}/src/test/resources/csv/data.csv")
      )

      target.size must be_==(1)
      target(0)._2.get.a0 must be_==("10")
      target(0)._2.get.a1 must be_==("20")
      target(0)._2.get.a2 must be_==("30")

    }

    "Get 1 row" in {
      val target = CsvMapper.by[CSV_Data]
        .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_1)
        .getCC(
          new FileInputStream(s"${currentDir}/src/test/resources/csv/data.csv")
        )

      target.size must be_==(1)
      target(0)._2.get.a0 must be_==("A")
      target(0)._2.get.a1 must be_==("B")
      target(0)._2.get.a2 must be_==("C")

    }

    "Get Down 2 row" in {
      val target = CsvMapper.by[CSV_Data]
        .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_0)
        .getCCDown(
          new FileInputStream(s"${currentDir}/src/test/resources/csv/data.csv")
        )

      target.size must be_==(1)
      target(0)._2(0).get.a0 must be_==("10")
      target(0)._2(0).get.a1 must be_==("20")
      target(0)._2(0).get.a2 must be_==("30")
      target(0)._2(1).get.a0 must be_==("A")
      target(0)._2(1).get.a1 must be_==("B")
      target(0)._2(1).get.a2 must be_==("C")

    }



  }

}
