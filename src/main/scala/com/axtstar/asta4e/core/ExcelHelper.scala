package com.axtstar.asta4e.core

import shapeless._

object ExcelHelper extends Helper {

  class ExcelHelper[A] {
    def fromAsOption[R <: HList](m: Map[String, Any])(implicit
                                                      gen: LabelledGeneric.Aux[A, R],
                                                      fromMap: FromMap[R]
    ): Option[A] = {
      val target = fromMap(m.map { mm => mm._1 -> Option(mm._2) }).map {
        x =>
          gen.from(x)
      }
      target
    }


    def from[R <: HList](m: Map[String, Any])(implicit
                                              gen: LabelledGeneric.Aux[A, R],
                                              fromMap: FromMap[R]
    ): Option[A] = {
      val target = fromMap(m).map {
        x =>
          gen.from(x)
      }
      target
    }

  }

  def to[A]: ExcelHelper[A] = {
    val target = new ExcelHelper[A]
    target
  }
}