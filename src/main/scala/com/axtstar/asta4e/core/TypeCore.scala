package com.axtstar.asta4e.core

import java.io.FileInputStream

import com.axtstar.asta4e.converter.CC.{FromMap, nullPoly, typeablePoly}
import shapeless.ops.hlist
import shapeless.{HList, LabelledGeneric, Typeable}
import shapeless.ops.record.{Keys, ToMap, Values}

trait TypeCore[A1] {

  def setCC[RA1 <: HList](bindCC:IndexedSeq[(String,Option[A1])])
                         (implicit
               gen: LabelledGeneric.Aux[A1, RA1],
               tmr: ToMap[RA1]):Unit

  def setCCDown[RA1 <: HList](bindData: IndexedSeq[(String,IndexedSeq[Option[A1]])])
                           (implicit
                            gen: LabelledGeneric.Aux[A1, RA1],
                            tmr: ToMap[RA1]):Unit

  def getCC[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList](
                  iStream: FileInputStream
                )(implicit gen: LabelledGeneric.Aux[A1, RA1],
                  fromMap: FromMap[RA1],
                  typeable: Typeable[A1],
                  keys: Keys.Aux[RA1, KA1],
                  ktl: hlist.ToList[KA1, Symbol],
                  values: Values.Aux[RA1, VA1],
                  mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                  fillWith: hlist.FillWith[nullPoly.type, VA1],
                  vtl: hlist.ToList[MA1, String]

  ):IndexedSeq[(String,Option[A1])]

  def getCCDown[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList](iStream:FileInputStream)
                    (implicit gen: LabelledGeneric.Aux[A1, RA1],
                     fromMap: FromMap[RA1],
                     typeable: Typeable[A1],
                     keys: Keys.Aux[RA1, KA1],
                     ktl: hlist.ToList[KA1, Symbol],
                     values: Values.Aux[RA1, VA1],
                     mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                     fillWith: hlist.FillWith[nullPoly.type, VA1],
                     vtl: hlist.ToList[MA1, String]

                    ):IndexedSeq[(String, IndexedSeq[Option[A1]])]

}
