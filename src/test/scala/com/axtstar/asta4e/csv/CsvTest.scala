package com.axtstar.asta4e.csv

import java.io.{File, FileInputStream}
import java.text.SimpleDateFormat

import com.axtstar.asta4e.{CsvMapper, ExcelMapper}
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

    "Set 1 row" in {
      import com.axtstar.asta4e.converter.E._

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")


      val target = CsvMapper.by[CSV_Data]
        .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_1)
        .withOutStream(s"${currentDir}/target/data_out_1.csv")
        ._setData(
          ("Sheet1" -> (
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
              )):_*
        ){
        x =>
            x
      }

      "" must be_==("")



    }




  }

}
