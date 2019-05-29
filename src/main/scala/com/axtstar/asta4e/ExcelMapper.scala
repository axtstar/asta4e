package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.core._
import shapeless._
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.converter.{CC, MapHelper}
import shapeless.LabelledGeneric.Aux
import shapeless.ops.hlist
import shapeless.syntax.typeable


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


  override def withOutStream(_outputXls: FileOutputStream) = {
    super.withOutStream(_outputXls).asInstanceOf[ExcelMapper[A]]
  }

  def withOutXls(_outputXlsPath: String) = {
    super.withOutStream(new FileOutputStream(_outputXlsPath)).asInstanceOf[ExcelMapper[A]]
  }

  import ops.record._

  def setCC[L <: HList](bindCC: IndexedSeq[(String, Option[A])])
                                   (implicit gen: Aux[A, L],
                                    tmr: ToMap[L]): Unit = {
    val map = bindCC.map {
      x =>
        x._1 -> CC.By(x._2.get).toMap
    }
    super._setData(map:_*)

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

    super._setDataDown(map:_*)

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

  override def getCC[R <: HList, K <: HList, V <: HList, V1 <: HList](iStream:FileInputStream)
                           (implicit gen: LabelledGeneric.Aux[A, R],
                              fromMap: FromMap[R],
                              typeable: Typeable[A],
                              keys: Keys.Aux[R, K],
                              ktl: hlist.ToList[K, Symbol],
                              values: Values.Aux[R, V],
                              mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                              fillWith: hlist.FillWith[nullPoly.type, V],
                              vtl: hlist.ToList[V1, String]

                           )={

    val columns = ktl(keys())

    super._getData(iStream).map {
      x =>

      //Type cast
        val m = x._2

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

        x._1 -> Option(
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
      )
      //
    }
  }

  override def getCCDown[R <: HList](iStream: FileInputStream)
                                    (implicit gen: Aux[A, R], fromMap: FromMap[R]): IndexedSeq[(String, IndexedSeq[Option[A]])] = {

    super._getDataDown(iStream).map {
      x =>
        x._1 ->
          x._2.map {
            xx =>
              fromMap(xx).map {
                xxx =>
                  gen.from(xxx)
              }
          }
    }
  }
  //End ExcelMapper[A]
}
