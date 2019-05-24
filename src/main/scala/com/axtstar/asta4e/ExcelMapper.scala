package com.axtstar.asta4e

import com.axtstar.asta4e.core.ExcelBasic
import shapeless._
import com.axtstar.asta4e.converter.CC._


object ExcelMapper extends ExcelBasic {

  /**
    * case class to Map
    *
    * @param a
    * @tparam A
    */
  implicit class By[A](val a: A) extends AnyVal {

    import ops.record._

    def toMap[L <: HList](implicit
                          gen: LabelledGeneric.Aux[A, L],
                          tmr: ToMap[L]
                         ): Map[String, Any] = {
      val m: Map[tmr.Key, tmr.Value] = tmr(gen.to(a))
      m.map {
        case (k: Symbol, n: None.type) =>
          k.name -> null
        case (k: Symbol, Some(v)) =>
          k.name -> v
        case (k: Symbol, v) =>
          k.name -> v
      }
    }
  }


  /**
    *
    * @tparam A
    */
  class ExcelMapper[A] extends ExcelBasic {

    import ops.record._

    def setData[L <: HList](
                                dataTemplateXls: String,
                                outLayout: String,
                                outXlsPath: String,
                                a:IndexedSeq[(String,Option[A])]
                                 )(implicit
                                   gen: LabelledGeneric.Aux[A, L],
                                   tmr: ToMap[L]
                                 ) = {

      val aToMap = a.map {
        x =>
          x._1 -> By(x._2.get).toMap
      }

      super.setData(
        dataTemplateXls,
        outLayout,
        outXlsPath,
        aToMap: _*
      )
    }

    def setDataDown[L <: HList](
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
              By(aaa.get).toMap
          })
      }

      super.setDataDown(
        dataTemplateXls,
        outLayout,
        outXlsPath,
        aToMap: _*
      )
    }

    def getData[R <: HList](
                                  dataTemplateXls: String,
                                  inputXlsPath: String,
                                  ignoreSheet: List[String]
                                )(implicit gen: LabelledGeneric.Aux[A, R]
                                  , fromMap: FromMap[R]) = {
      val target = super.getData(
        dataTemplateXls,
        inputXlsPath,
        ignoreSheet
      )

      target.map {
        m =>

          val target = fromMap(m._2.map { mm => mm._1 -> mm._2 }).map {
            x =>
              gen.from(x)
          }

          m._1 -> target
      }
    }

    def getDataDown[R <: HList](
                                       dataTemplateXls: String,
                                       inputXlsPath: String,
                                       ignoreSheet: List[String]

                                     )(implicit gen: LabelledGeneric.Aux[A, R]
                                       , fromMap: FromMap[R]) = {
      val target = super.getDataDown(dataTemplateXls, inputXlsPath, ignoreSheet)
      val result = target.map {
        x =>
          x._1 -> x._2.map {
            xx =>
              fromMap(xx).map {
                xxx =>
                  gen.from(xxx)
              }
          }
      }
      result
    }
    //End ExcelMapper[A]
  }

  def by[A]: ExcelMapper[A] = {
    val target = new ExcelMapper[A]
    target
  }
}
