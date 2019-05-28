package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.converter.CC
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.core._
import shapeless.LabelledGeneric.Aux
import shapeless._
import shapeless.ops.record.ToMap


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
class CsvMapper[A] extends CsvBasic with TypeCore[A] {
  def withLocation(_locationMap: List[Location]) = {
    super.withLocation(_locationMap)
    this
  }

  override def withLocation(_locationMapPathExcel: String) = {
    super.withLocation(_locationMapPathExcel)
    this
  }

  override def withOutStream(_outputXls: FileOutputStream) = {
    super.withOutStream(_outputXls)
    this
  }

  override def withOutStream(_outputPath: String) = {
    this.outputStream = new FileOutputStream(_outputPath)
    this
  }


  def withOutXls(_outputXlsPath: String) = {
    super.withOutStream(new FileOutputStream(_outputXlsPath))
    this
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


  override def setCC[L <: HList](bindCC: IndexedSeq[(String, Option[A])])
                                (implicit gen: Aux[A, L], tmr: ToMap[L]): Unit = {

    val map = bindCC.map {
      x =>
        x._1 -> CC.By(
          (x._2.get)
        ).toMap
    }
    super._setData(map:_*){
      x =>
        x
    }
  }

  override def setCCDown[L <: HList](bindData: IndexedSeq[(String, IndexedSeq[Option[A]])])
                                    (implicit gen: Aux[A, L], tmr: ToMap[L]): Unit = {
    val map = bindData.map {
      x =>
        x._1 -> (x._2.map {
          xx =>
            CC.By(xx.get).toMap
        })
    }

    super._setDataDown(map:_*){
      x =>
        x
    }

  }


}
