package com.axtstar.asta4e

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.basic.CsvBasic
import com.axtstar.asta4e.converter.CC
import com.axtstar.asta4e.converter.CC._
import com.axtstar.asta4e.core._
import org.apache.commons.csv.QuoteMode
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
  * @tparam A1
  */
class CsvMapper[A1] extends CsvBasic with TypeCore[A1] with FTypeCore[A1] {

  override def withDelimiter(_delimiter:Char):CsvBasic={
    this.delimiter = _delimiter
    this
  }

  override def withSeparator(_separator:Char):CsvBasic={
    this.separator = _separator
    this
  }

  override def withQuoteMode(_quoteMode:QuoteMode):CsvBasic={
    this.quoteMode = _quoteMode
    this
  }

  def withLocation(_locationMap: List[com.axtstar.asta4e.etc.Location]) = {
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

  override def withEncoding(_encoding:String)={
    this.encoding = _encoding
    this
  }


  override def getCC[RA1 <: HList, K <: HList, V <: HList, V1 <: HList](iStream:FileInputStream)
                                                                     (implicit gen: LabelledGeneric.Aux[A1, RA1],
                                                                      fromMap: FromMap[RA1],
                                                                      typeable: Typeable[A1],
                                                                      keys: Keys.Aux[RA1, K],
                                                                      ktl: hlist.ToList[K, Symbol],
                                                                      values: Values.Aux[RA1, V],
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
                None.asInstanceOf[A1]
            }
          }
        )
      //
    }
  }

  def getCCDown[R <: HList, K <: HList, V <: HList, V1 <: HList](iStream: FileInputStream)
                                    (
                                      implicit gen: Aux[A1, R],
                                      fromMap: FromMap[R],
                                      typeable: Typeable[A1],
                                      keys: Keys.Aux[R, K],
                                      ktl: hlist.ToList[K, Symbol],
                                      values: Values.Aux[R, V],
                                      mapper: hlist.Mapper.Aux[typeablePoly.type, V, V1],
                                      fillWith: hlist.FillWith[nullPoly.type, V],
                                      vtl: hlist.ToList[V1, String]

                                    ): IndexedSeq[(String, IndexedSeq[Option[A1]])] = {
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
                    None.asInstanceOf[A1]
                }
              }
            )
          //

        }
    }
  }


  override def setCC[L <: HList](bindCC: IndexedSeq[(String, Option[A1])])
                                (implicit gen: Aux[A1, L], tmr: ToMap[L]): Unit = {

    val map = bindCC.map {
      x =>
        x._1 -> CC.By(
          (x._2.get)
        ).toMap
    }
    super._setData(map:_*)
  }

  override def setCCDown[L <: HList](bindData: IndexedSeq[(String, IndexedSeq[Option[A1]])])
                                    (implicit gen: Aux[A1, L], tmr: ToMap[L]): Unit = {
    val map = bindData.map {
      x =>
        x._1 -> (x._2.map {
          xx =>
            CC.By(xx.get).toMap
        })
    }

    super._setDataDown(map:_*)

  }

  override def getFCC[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList,
  A2, RA2 <: HList, KA2 <: HList, VA2 <: HList, MA2 <: HList]
  (iStream: FileInputStream)
  (f: Option[A2] => Option[A1])
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

  ): IndexedSeq[(String, Option[A1])]={
    val columns = ktl2(keys2())

    super._getData(iStream).map {
      x =>

        //Type cast
        val m = x._2

        val target = fromMap2(columns.map { x =>
          x.name -> (
            if (m.contains(x.name)) {
              m(x.name)
            } else {
              null
            })
        }.toMap).map {
          x =>
            gen2.from(x)
        }

        x._1 -> f(Option(
          if (typeable2.describe.startsWith("Option")) {
            target.get
          } else {
            target match {
              case Some(tt) =>
                tt
              case _ =>
                None.asInstanceOf[A2]
            }
          }
        ))
      //
    }

  }

  override def getFCCDown[RA1 <: HList, KA1 <: HList, VA1 <: HList, MA1 <: HList,
  A2, RA2 <: HList, KA2 <: HList, VA2 <: HList, MA2 <: HList]
  (iStream: FileInputStream)
  (f: Option[A2] => Option[A1])
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

  ): IndexedSeq[(String, IndexedSeq[Option[A1]])] = {
    val columns = ktl2(keys2())

    super._getDataDown(iStream).map {
      x =>
        x._1 ->
          x._2.map {
            xx =>
              //Type cast
              val m = xx

              val target = fromMap2(columns.map { xxx =>
                xxx.name -> (
                  if (m.contains(xxx.name)) {
                    m(xxx.name)
                  } else {
                    null
                  })
              }.toMap).map {
                x =>
                  gen2.from(x)
              }

              f(Option(
                if (typeable2.describe.startsWith("Option")) {
                  target.get
                } else {
                  target match {
                    case Some(tt) =>
                      tt
                    case _ =>
                      None.asInstanceOf[A2]
                  }
                }
              ))
            //

          }
    }

  }

  override def setFCC[RA1 <: HList, A2 , RA2 <: HList]
  (bindCC: IndexedSeq[(String, Option[A2])])
  (f: Option[A2] => Option[A1])
  (implicit
   gen: LabelledGeneric.Aux[A1, RA1],
   tmr: ToMap[RA1],

   gen2: LabelledGeneric.Aux[A2, RA2],
   tmr2: ToMap[RA2]

  ): Unit = {
    val map = bindCC.map {
      x =>
        x._1 -> CC.By(f(x._2).get).toMap
    }
    super._setData(map: _*)

  }

  override def setFCCDown[RA1 <: HList, A2, RA2 <: HList]
  (bindCC: IndexedSeq[(String, IndexedSeq[Option[A2]])])
  (f: Option[A2] => Option[A1])
  (implicit
   gen: LabelledGeneric.Aux[A1, RA1],
   tmr: ToMap[RA1],

   gen2: LabelledGeneric.Aux[A2, RA2],
   tmr2: ToMap[RA2]

  ): Unit = {
    val map = bindCC.map {
      x =>
        x._1 -> (x._2.map {
          xx =>
            CC.By(f(xx).get).toMap
        })
    }

    super._setDataDown(map: _*)

  }

}
