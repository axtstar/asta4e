package com.axtstar.asta4e.core

case class Location(
  name:String,
  positionX:Int,
  positionY:Int,
  bindNames:List[String])

object Location{
  def create(  name:String,
               positionX:Int,
               positionY:Int) = {
    Location(name, positionX, positionY,bindNames = List("${" + name +"}"))
  }

  def create(  name:String,
               positionX:Int,
               positionY:Int,
               bindNames:List[String]) = {
    Location(name, positionX, positionY,bindNames)
  }
}
