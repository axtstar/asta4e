package com.axtstar.asta4e.excel

import java.io.{File, FileInputStream}
import java.text.SimpleDateFormat
import java.util.Date

import com.axtstar.asta4e.ExcelMapper
import com.axtstar.asta4e.converter._
import com.axtstar.asta4e.test_class._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
@RunWith(classOf[JUnitRunner])
class MapHelperTest extends Specification {

  val currentDir = new File(".").getAbsoluteFile.getParent


  "ExcelHelper" should {

    val v1 = VariousCell(string = "",
      int = 1,
      long = 2L,
      date = new Date(),
      boolean = true,
      float = 3F,
      double = 4D,
      formula = "formula",
      stringOpt = Some("stringOpt"),
      intOpt = Some(5),
      longOpt = Some(6L),
      dateOpt = Some(new Date()),
      booleanOpt = Some(true),
      floatOpt = Some(7F),
      doubleOpt = Some(8D),
      formulaOpt = Some("formulaOpt")
    )


    "to" in {
      //MapHelper.to[VariousCell].by[VariousCell]()
      MapHelper.to[VariousCell].isInstanceOf[MapHelper[VariousCell]] must be_==(true)
    }

    "from 2 to" should {

      "same signature and same type" in {
        val target = MapHelper.to[VariousCell_same].from(v1)
        target.string must be_==(v1.string)
        target.boolean must be_==(v1.boolean)
        target.float must be_==(v1.float)
      }

      "same signature but different type" in {
        val target = MapHelper.to[VariousCell_same_but_string].from(v1)
        target.string must be_==(v1.string)
        target.boolean must be_==(v1.boolean.toString)
        target.float must be_==(v1.float.toString)

      }

      "less signature and same type" in {
        val target = MapHelper.to[VariousCell_less].from(v1)
        target.string must be_==(v1.string)
        target.boolean must be_==(v1.boolean)
      }

      "less signature but different type" in {
        val target = MapHelper.to[VariousCell_less_but_string].from(v1)
        target.string must be_==(v1.string)
        target.boolean must be_==(v1.boolean.toString)
      }

      "more signature" in {
        val target = MapHelper.to[VariousCell_more].from(v1)
        target.string must be_==(v1.string)
        target.boolean must be_==(v1.boolean)
      }

    }

    "map to A" in {
      case class Data(
                     name:String,
                     address:String)

      val map = Map(
        "name" -> "axtstar",
        "address" -> "Tokyo, Japan"
      )

      val result = MapHelper.to[Data].from(map)

      result.name must be_==("axtstar")
      result.address must be_==("Tokyo, Japan")
    }


    "cast from another type" in {

      //toMap
      val target = ExcelMapper.apply()
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
        ._getData(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_excel_mapper.xlsx")){
          x =>
            x
        }

      val m = target.head._2
      val result = MapHelper.to[VariousCell].from(m)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      //string	int	long	date	boolean	float	double	formula
      //STRING	11101	11101	2019/6/1	TRUE	1110.1	1.000009	5
      result.string must be_==("STRING")
      result.int must be_==(11101.0D)
      result.long must be_==(11101.0D)
      result.date must be_==(dateFormat.parse("2019/6/1"))
      result.boolean must be_==(true)
      result.float must be_==(1110.1F)
      result.double must be_==(1.000009D)
      result.formula must be_==("5.0")
      result.stringOpt must be_==(Some("STRING1"))
      result.intOpt must be_==(Some(11101.0D))
      result.longOpt must be_==(Some(11101.0D))
      result.dateOpt must be_==(Some(dateFormat.parse("2019/6/1")))
      result.booleanOpt must be_==(Some(true))
      result.floatOpt must be_==(Some(1110.1F))
      result.doubleOpt must be_==(Some(1.000009D))
      result.formulaOpt must be_==(Some("5.0"))
    }

    "cast from null" in {

      //toMap
      val target = ExcelMapper.apply()
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_excel_mapper.xlsx")
        ._getData(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_excel_null_value.xlsx")){
          x =>
            x
        }

      val result = MapHelper.to[VariousCell].from(target.head._2)

      val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
      val timeFormat = new SimpleDateFormat("HH:mm:ss")

      val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

      //string	int	long	date	boolean	float	double	formula
      //STRING	11101	11101	2019/6/1	TRUE	1110.1	1.000009	5
      result.string must be_==("")
      result.int must be_==(0)
      result.long must be_==(0L)
      result.date must be_==(new Date(Long.MinValue))
      result.boolean must be_==(false)
      result.float must be_==(0F)
      result.double must be_==(0D)
      result.formula must be_==("")
      result.stringOpt must be_==(None)
      result.intOpt must be_==(None)
      result.longOpt must be_==(None)
      result.dateOpt must be_==(None)
      result.booleanOpt must be_==(None)
      result.floatOpt must be_==(None)
      result.doubleOpt must be_==(None)
      result.formulaOpt must be_==(None)
    }

  }


}
