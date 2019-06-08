package com.axtstar.asta4e.etc

import scala.util.matching.Regex

/**
  *
  * @param name bindName
  * @param positionX (start) position x of excel or csv's location
  * @param positionY (start) position y of excel or csv's location
  * @param original original expression from excel file, it's no necessary currently
  * @param expression regular expression when parsing a cell/column of excel/csv
  * @param bindNames dollar-bracket bind sets like List("${name1}","${data1}")
  */
case class Location(
                     /**
                       * bind name
                       */
                     name:String,
                     /**
                       * Location X position
                       */
                     positionX:Int,
                     /**
                       * Location Y position
                       */
                     positionY:Int,
                     /**
                       * Original word
                       */
                     original:String,
                     /**
                       *
                       */
                     expression:Option[Regex],
                     /**
                       *
                       */
                     bindNames:List[String])

object Location{
  def create(  name:String,
               positionX:Int,
               positionY:Int) = {
    Location(
      name,
      positionX,
      positionY,
      original = "",
      expression = None,
      bindNames = List("${" + name +"}"))
  }

  def create(  name:String,
               positionX:Int,
               positionY:Int,
               bindNames:List[String]) = {
    Location(
      name,
      positionX,
      positionY,
      original = "",
      expression = None, bindNames)
  }
}
