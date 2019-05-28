package com.axtstar.asta4e.core

trait DataCore[A] {
  protected var locationMap : List[Location] = List()
  def withLocation(_locationMap:List[Location])
                  (implicit ev:A  <:< DataCore[A]) ={
    this.locationMap= _locationMap
    this.asInstanceOf[A]
  }
}
