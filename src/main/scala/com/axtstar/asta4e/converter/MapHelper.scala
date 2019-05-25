package com.axtstar.asta4e.converter

import com.axtstar.asta4e.converter.CC.FromMap
import shapeless.ops.record.ToMap
import shapeless.{HList, LabelledGeneric, Typeable, ops}

object MapHelper extends MapHelper {

  /**
    * case class to Map
    *
    * @param a
    * @tparam A
    */
  implicit class By[A](val a: A) extends AnyVal {

    import ops.record._

    def toMap[L <: HList](implicit
                          gen: LabelledGeneric.Aux[A, L],
                          tmr: ToMap[L]
                         ): Map[String, Any] = {
      val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(a))
      m.map {
        case (k: Symbol, n: None.type) =>
          k.name -> null
        case (k: Symbol, Some(v)) =>
          k.name -> v
        case (k: Symbol, v) =>
          k.name -> v
      }
    }
  }


  def to[A]: MapHelper[A] = {
    val target = new MapHelper[A]
    target
  }
}


class MapHelper[A] {
/*
  def from[A,T,R <: HList,R1 <: HList](t:T)(implicit
                              genA: LabelledGeneric.Aux[A, R1],
                              fromMapA: FromMap[R],
                              typeableA: Typeable[A],
                              genT: LabelledGeneric.Aux[T, R1],
                              fromMapT: FromMap[R1],
                              typeableT: Typeable[T]
  ):A={

    genA.from(new Map("",""))
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

