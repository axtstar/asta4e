package com.axtstar.asta4e.test_class

import java.util.Date

case class Etc7Comp(
              numeric: Option[Double],
              string:String,
              date:Date = new Date(),
              formula:String,
              bool:Boolean = true,
              time:Date,
              userDate:Option[Date]
){
  val result : Boolean = false
  def getResult = {
    result
  }
}
