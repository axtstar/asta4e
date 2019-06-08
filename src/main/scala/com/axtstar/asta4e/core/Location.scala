package com.axtstar.asta4e.core

/**
  * deprecated use com.axtstar.asta4e.etc.Location instead.
  * @param name
  * @param positionX
  * @param positionY
  * @param bindNames
  */
@deprecated("this method will be removed", "0.16.0")
case class Location(
  name:String,
  positionX:Int,
  positionY:Int,
  bindNames:List[String])

@deprecated("this method will be removed", "0.16.0")
object Location{
  @deprecated("this method will be removed", "0.16.0")
  def create(  name:String,
               positionX:Int,
               positionY:Int) = {
    Location(name, positionX, positionY,bindNames = List("${" + name +"}"))
  }

  @deprecated("this method will be removed", "0.16.0")
  def create(  name:String,
               positionX:Int,
               positionY:Int,
               bindNames:List[String]) = {
    Location(name, positionX, positionY,bindNames)
  }
}
