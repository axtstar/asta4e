package com.axtstar.asta4e.core

import shapeless._
import com.axtstar.asta4e.converter.CC._

object ExcelHelper {

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
                                              fromMap: FromMap[R],
                                              typeable: Typeable[A]
    ): A = {

      val target = fromMap( m.map{ mm =>
        mm._1 -> mm._2
      }).map {
        x =>
          gen.from(x)
      }
      if(typeable.describe.startsWith("Option")){
        target.get
      } else {
        target.get
      }
    }

  }

  def to[A]: ExcelHelper[A] = {
    val target = new ExcelHelper[A]
    target
  }
}