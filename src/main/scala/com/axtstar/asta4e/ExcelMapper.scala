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
  * @tparam A1
  */
class ExcelMapper[A1] extends ExcelBasic with TypeCore[A1] {

  def withLocation(_locationMap: List[Location]) = {
    super.withLocation(_locationMap).asInstanceOf[ExcelMapper[A1]]
  }

  override def withLocation(_locationMapPathExcel: String) = {
    super.withLocation(_locationMapPathExcel).asInstanceOf[ExcelMapper[A1]]
  }

  override def withIgnoreSheets(_ignoreSheets: List[String]) = {
    super.withIgnoreSheets(_ignoreSheets).asInstanceOf[ExcelMapper[A1]]
  }

  override def withLayoutXls(_layoutXls: FileInputStream) = {
    super.withLayoutXls(_layoutXls).asInstanceOf[ExcelMapper[A1]]
  }

  def withLayoutXls(_layoutXlsPath: String) = {
    super.withLayoutXls(new FileInputStream(_layoutXlsPath)).asInstanceOf[ExcelMapper[A1]]
  }


  override def withOutStream(_outputXls: FileOutputStream) = {
    super.withOutStream(_outputXls).asInstanceOf[ExcelMapper[A1]]
  }

  def withOutXls(_outputXlsPath: String) = {
    super.withOutStream(new FileOutputStream(_outputXlsPath)).asInstanceOf[ExcelMapper[A1]]
  }

  import ops.record._

  override def setCC[RA1 <: HList](bindCC: IndexedSeq[(String, Option[A1])])
                                   (implicit
                                    gen: Aux[A1, RA1],
                                    tmr: ToMap[RA1]

                                   ): Unit = {
    val map = bindCC.map {
      x =>
        x._1 -> CC.By(x._2.get).toMap
    }
    super._setData(map:_*)

  }

  override def setCCDown[RA1 <: HList](bindData: IndexedSeq[(String, IndexedSeq[Option[A1]])])
                                    (implicit
                                     gen: Aux[A1, RA1],
                                     tmr: ToMap[RA1]

                                    ): Unit = {
    val map = bindData.map {
      x =>
        x._1 -> (x._2.map {
          xx =>
            CC.By(xx.get).toMap
        })
    }

    super._setDataDown(map:_*)

  }

  override def getCC[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList](iStream:FileInputStream)
                           (implicit gen: LabelledGeneric.Aux[A1, RA1],
                              fromMap: FromMap[RA1],
                              typeable: Typeable[A1],
                              keys: Keys.Aux[RA1, KA1],
                              ktl: hlist.ToList[KA1, Symbol],
                              values: Values.Aux[RA1, VA1],
                              mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                              fillWith: hlist.FillWith[nullPoly.type, VA1],
                              vtl: hlist.ToList[MA1, String]

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
              None.asInstanceOf[A1]
          }
        }
      )
      //
    }
  }

  override def getCCDown[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList](iStream: FileInputStream)
                                    (implicit
                                     gen: Aux[A1, RA1],
                                     fromMap: FromMap[RA1],
                                     typeable: Typeable[A1],
                                     keys: Keys.Aux[RA1, KA1],
                                     ktl: hlist.ToList[KA1, Symbol],
                                     values: Values.Aux[RA1, VA1],
                                     mapper: hlist.Mapper.Aux[typeablePoly.type, VA1, MA1],
                                     fillWith: hlist.FillWith[nullPoly.type, VA1],
                                     vtl: hlist.ToList[MA1, String]

                                    ): IndexedSeq[(String, IndexedSeq[Option[A1]])] = {

    val columns = ktl(keys())

    super._getDataDown(iStream).map {
      x =>
        x._1 ->
          x._2.map {
            xx =>
              //Type cast
              val m = xx

              val target = fromMap( columns.map{ xxx =>
                xxx.name -> (
                  if(m.contains(xxx.name)){
                    m(xxx.name)
                  } else {
                    null
                  })
              }.toMap).map {
                x =>
                  gen.from(x)
              }

              Option(
                if(typeable.describe.startsWith("Option")){
                  target.get
                } else {
                  target match {
                    case Some(tt) =>
                      tt
                    case _ =>
                      None.asInstanceOf[A1]
                  }
                }
              )
            //

          }
    }
  }
  //End ExcelMapper[A1]
}
