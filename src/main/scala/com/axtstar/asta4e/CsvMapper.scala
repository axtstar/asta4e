package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.converter.CC
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.core._
import shapeless.LabelledGeneric.Aux
import shapeless._
import shapeless.ops.hlist
import shapeless.ops.record.{Keys, ToMap, Values}


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

  def getCCDown[R <: HList, K <: HList, V <: HList, V1 <: HList](iStream: FileInputStream)
                                    (
                                      implicit gen: Aux[A, R],
                                      fromMap: FromMap[R],
                                      typeable: Typeable[A],
                                      keys: Keys.Aux[R, K],
                                      ktl: hlist.ToList[K, Symbol],
                                      values: Values.Aux[R, V],
                                      mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                                      fillWith: hlist.FillWith[nullPoly.type, V],
                                      vtl: hlist.ToList[V1, String]

                                    ): IndexedSeq[(String, IndexedSeq[Option[A]])] = {
    val columns = ktl(keys())

    super._getDataDown(iStream).map{
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
                    None.asInstanceOf[A]
                }
              }
            )
          //

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


}
