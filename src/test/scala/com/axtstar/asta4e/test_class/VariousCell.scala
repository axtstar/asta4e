package com.axtstar.asta4e.test_class

import java.util.Date

case class VariousCell(
              string:String,
              int:Int,
              long:Long,
              date:Date = new Date(),
              boolean:Boolean = true,
              float:Float,
              double:Double,
              formula:String,
              stringOpt:Option[String]
){
  val result : Boolean = false
  def getResult = {
    result
  }
}
