package com.axtstar.asta4e

import com.axtstar.asta4e.core.{ExcelBasic, Helper}
import shapeless._

object ExcelMapper extends ExcelBasic with Helper {

  /**
    * case class to Map
    *
    * @param a
    * @tparam A
    */
  implicit class ToMapOps[A](val a: A) extends AnyVal {

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
        case _ =>
          throw new IllegalArgumentException
      }
    }
  }


  /**
    *
    * @tparam A
    */
  class ExcelMapper[A] extends ExcelBasic {

    def getDataAsAny[R <: HList](
                                  dataTemplateXls: String,
                                  inputXlsPath: String,
                                  ignoreSheet: List[String]
                                )(implicit gen: LabelledGeneric.Aux[A, R]
                                  , fromMap: FromMap[R]) = {
      val target = getData(
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

    def getDataAsOption[R <: HList](
                                     dataTemplateXls: String,
                                     inputXlsPath: String,
                                     ignoreSheet: List[String]
                                   )(implicit gen: LabelledGeneric.Aux[A, R]
                                     , fromMap: FromMap[R]) = {
      val target = getData(
        dataTemplateXls,
        inputXlsPath,
        ignoreSheet
      )

      target.map {
        m =>

          println(fromMap.getClass.getSimpleName)
          val frm = fromMap(m._2.map {
            mm =>
              mm._1 -> Option(mm._2)
          })
          val target = frm.map {
            x =>
              gen.from(x)
          }

          m._1 -> target
      }
    }

    //End ExcelMapper[A]
  }

  def to[A]: ExcelMapper[A] = {
    val target = new ExcelMapper[A]
    target
  }

}
