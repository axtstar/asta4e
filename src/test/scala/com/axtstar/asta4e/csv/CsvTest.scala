package com.axtstar.asta4e.csv

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.Files
import java.nio.file.attribute.FileAttribute
import java.text.SimpleDateFormat
import java.util.Date

import com.axtstar.asta4e.converter.MapHelper
import com.axtstar.asta4e.test_class.{VariousCell, VariousCell_less}
import com.axtstar.asta4e.{CsvMapper, ExcelMapper}
import com.axtstar.asta4e.test_location.{CSV_Data, Location_4_CSV}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CsvTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "CSV" should {
    "Not Found" in {
      def a = {
        val target = CsvMapper.by[CSV_Data]
          .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_0)
          .getCC(
            new FileInputStream(s"${currentDir}/src/test/resources/csv/notfound")
          )
      }
      a must throwA[java.io.IOException]

      def b = {
        val target = CsvMapper.by[CSV_Data]
          .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_0)
          .getCCDown(
            new FileInputStream(s"${currentDir}/src/test/resources/csv/notfound")
          )
      }
      b must throwA[java.io.IOException]

      def c = {
        CsvMapper.by[VariousCell]
          .withLocation(VariousCell.getLocation())
          .withOutStream(new FileOutputStream(s"${currentDir}/target///notfound"))
          .setCC(IndexedSeq("Sheet1" -> Option(null.asInstanceOf[VariousCell]))) // Error
      }
      c must throwA[java.util.NoSuchElementException]

    }


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

    "SetCC -> Get 1 row" in {
      import com.axtstar.asta4e.converter.E._

      val map = MapHelper.to[VariousCell].from("numeric" -> 1001 &
        "string" -> "1000" &
        "date" -> new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7") &
        "formula" -> "=B2" &
        "bool" -> true &
        "time" -> new SimpleDateFormat("HH:mm:ss").parse("23:32:41") &
        "userDate" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2018/11/23 18:52:56")
      )

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","data_w_r_1.csv")
      println(ff.getAbsolutePath)

      CsvMapper.by[VariousCell]
        .withLocation(VariousCell.getLocation())
        .withOutStream(ff.getAbsolutePath)
        .setCC(IndexedSeq("Sheet1" -> Option(map)))


      val target = CsvMapper.by[VariousCell]
        .withLocation(VariousCell.getLocation())
        .getCC(
          new FileInputStream(ff.getAbsolutePath)
        )

      target.size must be_==(1)
      target(0)._2.get.string must be_==("1000")
      //println(target(0)._2.get.date)
      target(0)._2.get.date must be_==(new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7"))
      target(0)._2.get.formula must be_==("") //Location設定なし
    }

    "SetFCC -> FCC 1 row" in {
      import com.axtstar.asta4e.converter.E._

      val map = MapHelper.to[VariousCell].from("numeric" -> 1001 &
        "string" -> "1000" &
        "date" -> new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7") &
        "formula" -> "=B2" &
        "bool" -> true &
        "time" -> new SimpleDateFormat("HH:mm:ss").parse("23:32:41") &
        "userDate" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2018/11/23 18:52:56")
      )

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","data_w_r_1.csv")

      CsvMapper.by[VariousCell_less]
        .withLocation(VariousCell.getLocation())
        .withOutStream(ff.getAbsolutePath)
        .setFCC(IndexedSeq("Sheet1" -> Option(map))){
          x:Option[VariousCell] =>
            val result = MapHelper.to[VariousCell_less].from(x.get)
            Option(result)
        }


      val target = CsvMapper.by[VariousCell]
        .withLocation(VariousCell.getLocation())
        .getFCC(
          new FileInputStream(ff.getAbsolutePath)
        ){
          x:Option[VariousCell_less] =>
            val result = MapHelper.to[VariousCell].from(x.get)
            Option(result)
        }

      target.size must be_==(1)
      target(0)._2.get.string must be_==("1000")
      //removed date due to case class not define "date"
      target(0)._2.get.date must be_==(new Date(Long.MinValue))
      target(0)._2.get.formula must be_==("")
    }


    "SetDownCC -> GetDown 1 row" in {
      import com.axtstar.asta4e.converter.E._

      val map = MapHelper.to[VariousCell].from("numeric" -> 1001 &
        "string" -> "1000" &
        "date" -> new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7") &
        "formula" -> "=B2" &
        "bool" -> true &
        "time" -> new SimpleDateFormat("HH:mm:ss").parse("23:32:41") &
        "userDate" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2018/11/23 18:52:56")
      )

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","data_w_r_down.csv").getAbsolutePath

      CsvMapper.by[VariousCell]
        .withLocation(VariousCell.getLocation())
        .withOutStream(new FileOutputStream(ff))
        .setCCDown(IndexedSeq("Sheet1" -> IndexedSeq(Option(map))))

      val s = scala.io.Source.fromFile(ff)
      for (line <- s.getLines) {
        println(line)
      }
      s.close()

      val target = CsvMapper.by[VariousCell]
        .withLocation(VariousCell.getLocation())
        .getCCDown(
          new FileInputStream(ff)
        )

      println(target(0)._2(0).get.date)
      target.size must be_==(1)
      target(0)._2(0).get.string must be_==("1000")
      target(0)._2(0).get.date must be_==(new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7"))
      target(0)._2(0).get.formula must be_==("") //Location設定なし
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

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","data_out_1.csv")

      val target = CsvMapper.by[CSV_Data]
        .withLocation(Location_4_CSV.ao_a1_a2_startRow_as_1)
        .withOutStream(new FileOutputStream(ff.getAbsolutePath))
        ._setData(
          ("Sheet1" -> (
            "numeric" -> 1001 &
              "string" -> "1000" &
              "date" -> new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/7") &
              "formula" -> "=B2" &
              "bool" -> true &
              "time" -> new SimpleDateFormat("HH:mm:ss").parse("23:32:41") &
              "userDate" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2018/11/23 18:52:56")
            ) &
            "Sheet2" -> (
              "numeric" -> 1002 &
                "string" -> "1001" &
                "date" -> new SimpleDateFormat("yyyy/MM/dd").parse("2018/7/8") &
                "formula" -> "=B3" &
                "bool" -> false &
                "time" -> new SimpleDateFormat("HH:mm:ss").parse("23:32:42") &
                "userDate" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2018/11/24 18:52:56")
              )):_*
        )

      "" must be_==("")



    }

    "size" in {
      val target = CsvMapper
        .getColumnSize(
          new FileInputStream(s"${currentDir}/src/test/resources/csv/data.csv")
        )

      target must be_==(3)
    }

  }

}
