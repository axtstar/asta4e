package com.axtstar.asta4e.converter

import com.axtstar.asta4e.converter.CC.FromMap
import shapeless.ops.record.ToMap
import shapeless.{HList, LabelledGeneric, Typeable, ops}

object MapHelper extends MapHelper {

  def to[A]: MapHelper[A] = {
    val target = new MapHelper[A]
    target
  }
}


class MapHelper[A] {
/*
  def from[A,A1,L <: HList,R <: HList](t:A1)(implicit
                              genT: LabelledGeneric.Aux[A1, L],
                              tmrT: ToMap[L],

                              gen: LabelledGeneric.Aux[A, R],
                              fromMap: FromMap[R],
                              typeable: Typeable[A]
  ):A={
    val m = CC.By(t)
    val target = fromMap(m.toMap).map{
      x =>
        gen.from(x)
    }
    from(target)
  }
*/

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

