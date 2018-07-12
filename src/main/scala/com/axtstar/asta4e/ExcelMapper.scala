package com.axtstar.asta4e

import com.axtstar.asta4e.core.ExcelBasic
import shapeless._
import labelled.{FieldType, field}

object ExcelMapper extends ExcelBasic {

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

  trait FromMap[L <: HList] {
    def apply(m: Map[String, Any]): Option[L]
  }

  trait LowPriorityFromMap {
    implicit def hconsFromMap1[K <: Symbol, V, T <: HList](implicit
                                                           witness: Witness.Aux[K],
                                                           typeable: Typeable[V],
                                                           fromMapT: Lazy[FromMap[T]]
                                                          ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {

      def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = {
        val result = for {
          v <- m.get(witness.value.name)
          h <- typeable.cast(v)
          t <- fromMapT.value(m)
        } yield field[K](h) :: t
        result

      }
    }
  }

  object FromMap extends LowPriorityFromMap {
    implicit val hnilFromMap: FromMap[HNil] = new FromMap[HNil] {
      def apply(m: Map[String, Any]): Option[HNil] = Some(HNil)
    }

    implicit def hconsFromMap0[K <: Symbol, V, R <: HList, T <: HList](implicit
                                                                       witness: Witness.Aux[K],
                                                                       gen: LabelledGeneric.Aux[V, R],
                                                                       fromMapH: FromMap[R],
                                                                       fromMapT: FromMap[T]
                                                                      ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
      def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
        v <- m.get(witness.value.name)
        r <- Typeable[Map[String, Any]].cast(v)
        h <- fromMapH(r)
        t <- fromMapT(m)
      } yield field[K](gen.from(h)) :: t
    }
  }


  /**
    *
    * @tparam A
    */
  class ExcelMapper[A] extends ExcelBasic {

    def fromAsOption[R <: HList](m: Map[String, Any])(implicit
                                                      gen: LabelledGeneric.Aux[A, R],
                                                      fromMap: FromMap[R]
    ): Option[A] = {
      val target = fromMap(m.map { mm => mm._1 -> Option(mm._2) }).map {
        x =>
          gen.from(x)
      }
      target
    }


    def from[R <: HList](m: Map[String, Any])(implicit
                                              gen: LabelledGeneric.Aux[A, R],
                                              fromMap: FromMap[R]
    ): Option[A] = {
      val target = fromMap(m).map {
        x =>
          gen.from(x)
      }
      target
    }

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

          val frm = fromMap(m._2.map { mm => mm._1 -> Option(mm._2) })
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
