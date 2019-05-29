package com.axtstar.asta4e.test_class

import java.util.Date

import com.axtstar.asta4e.core.Location

case class VariousCell(
              string:String,// 0
              int:Int,// 1
              long:Long,// 2
              date:Date = new Date(),// 3
              boolean:Boolean = true,// 4
              float:Float,// 5
              double:Double,// 6
              formula:String,// 7
              stringOpt:Option[String],// 8
              intOpt:Option[Int],// 9
              longOpt:Option[Long],// 10
              dateOpt:Option[Date],// 11
              booleanOpt:Option[Boolean],// 12
              floatOpt:Option[Float],// 13
              doubleOpt:Option[Double],// 14
              formulaOpt:Option[String]// 15
)

object VariousCell {
  def getLocation()=List(
    Location.create(name="string",0,0),
    Location.create(name="int",1,0),
    Location.create(name="long",2,0),
    Location.create(name="date",3,0),
    Location.create(name="boolean",4,0),
    Location.create(name="float",5,0),
    Location.create(name="double",6,0),
    Location.create(name="longOpt",10,0)
    )
}
