package com.axtstar.asta4e.core

import java.io.FileInputStream

import com.axtstar.asta4e.converter.CC.{FromMap, nullPoly, typeablePoly}
import shapeless.ops.hlist
import shapeless.ops.record.{Keys, ToMap, Values}
import shapeless.{HList, LabelledGeneric, Typeable}

trait FTypeCore[A1] {

  def getFCC[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList,
          A2,RA2 <: HList, KA2 <: HList, VA2 <: HList, MA2 <: HList]
                (iStream: FileInputStream)
                (f:Option[A2] => Option[A1])
                (implicit
                  gen: LabelledGeneric.Aux[A1, RA1],
                  fromMap: FromMap[RA1],
                  typeable: Typeable[A1],
                  keys: Keys.Aux[RA1, KA1],
                  ktl: hlist.ToList[KA1, Symbol],
                  values: Values.Aux[RA1, VA1],
                  mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                  fillWith: hlist.FillWith[nullPoly.type, VA1],
                  vtl: hlist.ToList[MA1, String],

                  gen2: LabelledGeneric.Aux[A2, RA2],
                  fromMap2: FromMap[RA2],
                  typeable2: Typeable[A2],
                  keys2: Keys.Aux[RA2, KA2],
                  ktl2: hlist.ToList[KA2, Symbol],
                  values2: Values.Aux[RA2, VA2],
                  mapper2: hlist.Mapper.Aux[typeablePoly.type, VA2, MA2],
                  fillWith2: hlist.FillWith[nullPoly.type, VA2],
                  vtl2: hlist.ToList[MA2, String]

  ):IndexedSeq[(String,Option[A1])]

  def getFCCDown[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList,
              A2,RA2 <: HList, KA2 <: HList, VA2 <: HList, MA2 <: HList]
                (iStream:FileInputStream)
                (f:Option[A2] => Option[A1])
                (implicit
                   gen: LabelledGeneric.Aux[A1, RA1],
                   fromMap: FromMap[RA1],
                   typeable: Typeable[A1],
                   keys: Keys.Aux[RA1, KA1],
                   ktl: hlist.ToList[KA1, Symbol],
                   values: Values.Aux[RA1, VA1],
                   mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                   fillWith: hlist.FillWith[nullPoly.type, VA1],
                   vtl: hlist.ToList[MA1, String],

                   gen2: LabelledGeneric.Aux[A2, RA2],
                   fromMap2: FromMap[RA2],
                   typeable2: Typeable[A2],
                   keys2: Keys.Aux[RA2, KA2],
                   ktl2: hlist.ToList[KA2, Symbol],
                   values2: Values.Aux[RA2, VA2],
                   mapper2: hlist.Mapper.Aux[typeablePoly.type, VA2, MA2],
                   fillWith2: hlist.FillWith[nullPoly.type, VA2],
                   vtl2: hlist.ToList[MA2, String]

                ):IndexedSeq[(String, IndexedSeq[Option[A1]])]

  def setFCC[RA1 <: HList,A2]
                (bindCC:IndexedSeq[(String,Option[A1])])
                (f:Option[A2] => Option[A1])
                (implicit
                 gen: LabelledGeneric.Aux[A1, RA1],
                 tmr: ToMap[RA1],

                 gen2: LabelledGeneric.Aux[A1, RA1],
                 tmr2: ToMap[RA1]

                ):Unit

  def setFCCDown[RA1 <: HList,A2, RA2 <:HList]
                (bindData: IndexedSeq[(String,IndexedSeq[Option[A1]])])
                (f:Option[A2] => Option[A1])
                (implicit
                  gen: LabelledGeneric.Aux[A1, RA1],
                  tmr: ToMap[RA1],

                  gen2: LabelledGeneric.Aux[A2, RA2],
                  tmr2: ToMap[RA2]

                ):Unit

}
