package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.converter.CC
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.core._
import shapeless.LabelledGeneric.Aux
import shapeless._


object CsvMapper extends CsvBasic {

  def apply[A]()={
    val target = new CsvMapper[A]()
    target
  }

  def by[A]: CsvMapper[A] = {
    val target = new CsvMapper[A]()
    target
  }
}


/**
  *
  * @tparam A
  */
class CsvMapper[A] extends CsvBasic {

  def withLocation(_locationMap: List[Location]) = {
    super.withLocation(_locationMap).asInstanceOf[CsvMapper[A]]
  }

  override def withLocation(_locationMapPathExcel: String) = {
    super.withLocation(_locationMapPathExcel).asInstanceOf[CsvMapper[A]]
  }

  def getCC[R <: HList](iStream:FileInputStream)
                                (implicit gen: LabelledGeneric.Aux[A, R]
                                 , fromMap: FromMap[R])={
    super._getData(iStream){
      x =>
        fromMap(x).map{
          xx =>
            gen.from(xx)
        }
    }
  }

  def getCCDown[R <: HList](iStream: FileInputStream)
                                    (implicit gen: Aux[A, R], fromMap: FromMap[R]): IndexedSeq[(String, IndexedSeq[Option[A]])] = {

    super._getDataDown(iStream){
      x =>
        fromMap(x).map{
          xx =>
            gen.from(xx)
        }
    }

  }


}
