package com.axtstar.asta4e.converter

import com.axtstar.asta4e.converter.CC._
import shapeless.ops.hlist
import shapeless.ops.record.{Keys, ToMap, Values}
import shapeless.{HList, LabelledGeneric, Typeable, ops}

object MapHelper extends MapHelper {

  def to[A]: MapHelper[A] = {
    val target = new MapHelper[A]
    target
  }
}


class MapHelper[A] {

  def from[R <: HList,T,L <: HList, K <: HList, V <: HList, V1 <: HList](t:T)(implicit
                              genT: LabelledGeneric.Aux[T, L],
                              tmrT: ToMap[L],
                              fromMapT: FromMap[L],
                              typeableT: Typeable[T],
                              gen: LabelledGeneric.Aux[A, R],
                              fromMap: FromMap[R],
                              typeable: Typeable[A],
                              keys: Keys.Aux[R, K],
                              ktl: hlist.ToList[K, Symbol],
                              values: Values.Aux[R, V],
                              mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                              fillWith: hlist.FillWith[nullPoly.type, V],
                              vtl: hlist.ToList[V1, String]

  ):A={
    val m = CC.By(t)
    from(m.toMap)
  }

  /*

    trait Cpo[A] {

    def withPrimaryKey[R <: HList, K <: HList, V <: HList, V1 <: HList](f: Seq[Symbol] => Seq[Symbol])(implicit
      labellGeneric: LabelledGeneric.Aux[A, R],
      keys: Keys.Aux[R, K],
      ktl: hlist.ToList[K, Symbol],
      values: Values.Aux[R, V],
      mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
      fillWith: hlist.FillWith[nullPoly.type, V],
      vtl: hlist.ToList[V1, String]
    ): Cpo[A] = {
      println(ktl(keys())) // List('i, 's)
      println(vtl(mapper(fillWith()))) // List(Int, String)
      ???
    }
  }

   */


  def from[R <: HList, K <: HList, V <: HList, V1 <: HList](m: Map[String, Any])(implicit
                                            gen: LabelledGeneric.Aux[A, R],
                                            fromMap: FromMap[R],
                                            typeable: Typeable[A],
                                            keys: Keys.Aux[R, K],
                                            ktl: hlist.ToList[K, Symbol],
                                            values: Values.Aux[R, V],
                                            mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                                            fillWith: hlist.FillWith[nullPoly.type, V],
                                            vtl: hlist.ToList[V1, String]

  ): A = {

    val columns = ktl(keys())

    val target = fromMap( columns.map{ x =>
      x.name -> (
        if(m.contains(x.name)){
          m(x.name)
        } else {
          null
        })
    }.toMap).map {
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

