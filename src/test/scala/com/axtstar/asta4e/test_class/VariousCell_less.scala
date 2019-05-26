package com.axtstar.asta4e.test_class

import java.util.Date

case class VariousCell_less(
              string:String,
              long:Long,
              boolean:Boolean = true,
              double:Double,
              formula:String,
              stringOpt:Option[String],
              intOpt:Option[Int],
              dateOpt:Option[Date],
              formulaOpt:Option[String]
){
  val result : Boolean = false
  def getResult = {
    result
  }
}
