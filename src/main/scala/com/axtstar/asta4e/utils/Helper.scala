package com.axtstar.asta4e.utils

import com.axtstar.asta4e.ExcelMapper
import shapeless._
import labelled.{FieldType, field}

object Helper {

  /**
    * case class to Map
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
        case (k: Symbol, v) =>
          k.name -> v
        case _ =>
          throw new IllegalArgumentException
      }
    }

    def toExcel[L <: HList](
                             dataTemplateXls:String,
                             outTemplate:String,
                             outXlsPath:String)(implicit
                            gen: LabelledGeneric.Aux[A, L],
                            tmr: ToMap[L]) = {
      val target = toMap[L](gen,tmr)
      ExcelMapper.setDataAsTemplate(
        dataTemplateXls,
        outTemplate,
        outXlsPath,
        target
      )
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
  class ConvertHelper[A] {

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
  }

  def to[A]: ConvertHelper[A] = {
    val target = new ConvertHelper[A]
    target
  }

}