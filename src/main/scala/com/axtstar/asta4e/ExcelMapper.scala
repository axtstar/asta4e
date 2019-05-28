package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.core._
import shapeless._
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.converter.{CC, MapHelper}
import shapeless.LabelledGeneric.Aux


object ExcelMapper extends ExcelBasic {

  def apply[A]()={
    val target = new ExcelMapper[A]()
    target
  }

  def by[A]: ExcelMapper[A] = {
    val target = new ExcelMapper[A]()
    target
  }
}

/**
  *
  * @tparam A
  */
class ExcelMapper[A] extends ExcelBasic with TypeCore[A] {

  def withLocation(_locationMap: List[Location]) = {
    super.withLocation(_locationMap).asInstanceOf[ExcelMapper[A]]
  }

  override def withLocation(_locationMapPathExcel: String) = {
    super.withLocation(_locationMapPathExcel).asInstanceOf[ExcelMapper[A]]
  }

  override def withIgnoreSheets(_ignoreSheets: List[String]) = {
    super.withIgnoreSheets(_ignoreSheets).asInstanceOf[ExcelMapper[A]]
  }

  override def withLayoutXls(_layoutXls: FileInputStream) = {
    super.withLayoutXls(_layoutXls).asInstanceOf[ExcelMapper[A]]
  }

  def withLayoutXls(_layoutXlsPath: String) = {
    super.withLayoutXls(new FileInputStream(_layoutXlsPath)).asInstanceOf[ExcelMapper[A]]
  }


  override def withOutXls(_outputXls: FileOutputStream) = {
    super.withOutXls(_outputXls).asInstanceOf[ExcelMapper[A]]
  }

  def withOutXls(_outputXlsPath: String) = {
    super.withOutXls(new FileOutputStream(_outputXlsPath)).asInstanceOf[ExcelMapper[A]]
  }

  import ops.record._

  def setCC[L <: HList](bindCC: IndexedSeq[(String, Option[A])])
                                   (implicit gen: Aux[A, L],
                                    tmr: ToMap[L]): Unit = {
    val map = bindCC.map {
      x =>
        x._1 -> CC.By(x._2.get).toMap
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

  def setData[L <: HList,B](a:IndexedSeq[(String,Option[A])])
                           (f: Option[A] => B)
                           (implicit
                           gen: LabelledGeneric.Aux[A, L],
                           tmr: ToMap[L]
                         ) = {

    val aToMap = a.map {
      x =>
        x._1 -> CC.By(x._2.get).toMap
    }

    this.setCC(a)
  }

  def setDataDown[L <: HList,B](
                               dataTemplateXls: String,
                               outLayout: String,
                               outXlsPath: String,
                               a:IndexedSeq[(String,IndexedSeq[Option[A]])]
                             )(implicit
                               gen: LabelledGeneric.Aux[A, L],
                               tmr: ToMap[L]
                             ) = {

    val aToMap = a.map {
      aa =>
        aa._1 -> (aa._2.map {
          aaa =>
            CC.By(aaa.get).toMap
        })
    }

    super.withLocation(dataTemplateXls)
      .withLayoutXls(new FileInputStream(outLayout))
      .withOutXls(new FileOutputStream(outXlsPath))
      ._setDataDown(aToMap: _*){
        x =>
          x
      }
  }


  override def getCC[R <: HList](iStream:FileInputStream)
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

  override def getCCDown[R <: HList](iStream: FileInputStream)
                                    (implicit gen: Aux[A, R], fromMap: FromMap[R]): IndexedSeq[(String, IndexedSeq[Option[A]])] = {

    super._getDataDown(iStream){
      x =>
        fromMap(x).map{
          xx =>
            gen.from(xx)
        }
    }

  }

  def getData[R <: HList,B](
                           dataTemplateXls: String,
                           inputXlsPath: String,
                           ignoreSheet: List[String]
                         )(implicit gen: LabelledGeneric.Aux[A, R]
                           , fromMap: FromMap[R]) = {
    this.withLocation(ExcelBasic.getExcelLocation(dataTemplateXls))
      .withIgnoreSheets(ignoreSheet)
      .getCC(new FileInputStream(inputXlsPath))
  }

  def getDataDown[R <: HList](
                               dataTemplateXls: String,
                               inputXlsPath: String,
                               ignoreSheet: List[String]

                             )(implicit gen: LabelledGeneric.Aux[A, R]
                               , fromMap: FromMap[R]) = {
    super.withLocation(ExcelBasic.getExcelLocation(dataTemplateXls))
      .withIgnoreSheets(ignoreSheet)
      ._getDataDown(new FileInputStream(inputXlsPath)) {
        x =>
          fromMap(x).map {
            xx =>
              gen.from(xx)
          }

      }
  }
  //End ExcelMapper[A]
}
