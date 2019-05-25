package com.axtstar.asta4e.core

import shapeless._
import com.axtstar.asta4e.converter.CC._

object MapHelper {

  class MapHelper[A] {
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
        target match {
          case Some(tt) =>
            tt
          case _ =>
            None.asInstanceOf[A]
        }
      }
    }

  }

  def to[A]: MapHelper[A] = {
    val target = new MapHelper[A]
    target
  }
}