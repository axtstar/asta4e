package com.axtstar.asta4e.core

import java.io.FileInputStream

import com.axtstar.asta4e.converter.CC.FromMap
import shapeless.{HList, LabelledGeneric}
import shapeless.ops.record.ToMap

trait TypeCore[A] {

  def setCC[L <: HList](bindCC:IndexedSeq[(String,Option[A])])
                         (implicit
               gen: LabelledGeneric.Aux[A, L],
               tmr: ToMap[L]):Unit

  def setCCDown[L <: HList](bindData: IndexedSeq[(String,IndexedSeq[Option[A]])])
                           (implicit
                            gen: LabelledGeneric.Aux[A, L],
                            tmr: ToMap[L]):Unit

  def getCC[R <: HList](
                  iStream: FileInputStream
                )(implicit gen: LabelledGeneric.Aux[A, R]
                  , fromMap: FromMap[R]):IndexedSeq[(String,Option[A])]

  def getCCDown[R <: HList](iStream:FileInputStream)
                    (implicit gen: LabelledGeneric.Aux[A, R]
                     , fromMap: FromMap[R]):IndexedSeq[(String, IndexedSeq[Option[A]])]

}
