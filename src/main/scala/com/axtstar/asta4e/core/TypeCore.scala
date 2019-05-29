package com.axtstar.asta4e.core

import java.io.FileInputStream

import com.axtstar.asta4e.converter.CC.{FromMap, nullPoly, typeablePoly}
import shapeless.ops.hlist
import shapeless.{HList, LabelledGeneric, Typeable}
import shapeless.ops.record.{Keys, ToMap, Values}

trait TypeCore[A] {

  def setCC[L <: HList](bindCC:IndexedSeq[(String,Option[A])])
                         (implicit
               gen: LabelledGeneric.Aux[A, L],
               tmr: ToMap[L]):Unit

  def setCCDown[L <: HList](bindData: IndexedSeq[(String,IndexedSeq[Option[A]])])
                           (implicit
                            gen: LabelledGeneric.Aux[A, L],
                            tmr: ToMap[L]):Unit

  def getCC[R <: HList, K <: HList, V <: HList, V1 <: HList](
                  iStream: FileInputStream
                )(implicit gen: LabelledGeneric.Aux[A, R],
                  fromMap: FromMap[R],
                  typeable: Typeable[A],
                  keys: Keys.Aux[R, K],
                  ktl: hlist.ToList[K, Symbol],
                  values: Values.Aux[R, V],
                  mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                  fillWith: hlist.FillWith[nullPoly.type, V],
                  vtl: hlist.ToList[V1, String]

  ):IndexedSeq[(String,Option[A])]

  def getCCDown[R <: HList, K <: HList, V <: HList, V1 <: HList](iStream:FileInputStream)
                    (implicit gen: LabelledGeneric.Aux[A, R],
                     fromMap: FromMap[R],
                     typeable: Typeable[A],
                     keys: Keys.Aux[R, K],
                     ktl: hlist.ToList[K, Symbol],
                     values: Values.Aux[R, V],
                     mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                     fillWith: hlist.FillWith[nullPoly.type, V],
                     vtl: hlist.ToList[V1, String]

                    ):IndexedSeq[(String, IndexedSeq[Option[A]])]

}
