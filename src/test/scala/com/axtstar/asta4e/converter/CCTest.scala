package com.axtstar.asta4e.converter

import java.io.{File, FileInputStream}
import java.text.SimpleDateFormat
import java.util.Date

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CCTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
  val timeFormat = new SimpleDateFormat("HH:mm:ss")

  val dateFormatFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")


  "CC" should {
    "Streight convert" in {
      case class Data_string(a:String,b:Int,c:Short,d:Long,e:Byte,f:Char,g:Float,h:Double)
      case class Data_short(a:Short)
      case class Data_int(a:Int)
      case class Data_long(a:Long)
      case class Data_byte(a:Byte)
      case class Data_char(a:Char)
      case class Data_date(a:Date)
      case class Data_float(a:Float)
      case class Data_double(a:Double)

      import com.axtstar.asta4e.converter.E._
      val data = (
        "a" -> "9" &
          "b" -> 9 &
          "c" -> (9:Short) &
          "d" -> (9L) &
          "e" -> (9:Byte) &
          "f" -> ('9') &
          "g" -> (9f) &
          "h" -> (9d) &

          "sample" -> "sample")


      val target_string = MapHelper.to[Data_string].from(data)
      target_string.a must be_==("9")

      val target_short = MapHelper.to[Data_short].from(data)
      target_short.a must be_==(9:Short)

      val target_int = MapHelper.to[Data_int].from(data)
      target_int.a must be_==(9)

      val target_long = MapHelper.to[Data_long].from(data)
      target_long.a must be_==(9L)

      val target_byte = MapHelper.to[Data_byte].from(data)
      target_byte.a must be_==(9:Byte)

      val target_char = MapHelper.to[Data_char].from(data)
      target_char.a must be_==('9')

      val target_float = MapHelper.to[Data_float].from(data)
      target_float.a must be_==(9F)

      val target_double = MapHelper.to[Data_double].from(data)
      target_double.a must be_==(9D)





    }

  }

}
