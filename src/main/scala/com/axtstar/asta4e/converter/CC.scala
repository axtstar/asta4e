package com.axtstar.asta4e.converter

import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Typeable, Witness}
import shapeless.labelled.{FieldType, field}

object CC {
  trait FromMap[L <: HList] {
    def apply(m: Map[String, Any]): Option[L]
  }

  lazy val typeNameEx = "Option\\[(.*)\\]".r

  trait LowPriorityFromMap {
    implicit def hconsFromMap1[K <: Symbol, V, T <: HList](implicit
                                                           witness: Witness.Aux[K],
                                                           typeable: Typeable[V],
                                                           fromMapT: Lazy[FromMap[T]]
                                                          ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {

      private def getWitness(m: Map[String, Any], name:String, typename:String, isOption:Boolean):Option[Any]= {
        val result = typename match{
          case typeNameEx(c) =>
            getWitness(m, name, c, isOption)
          case _ =>
            //
            if(isOption){
              //option class
              m.get(name) match {
                case Some(null) =>
                  Option(None)
                case mm =>
                  Option(mm)
              }

            } else {
              //primitive class
              m.get(name) match {
                case Some(null) =>
                  //if not determine from data, asta4e convert them to wrapped primitive value
                  // TODO : need more precise way of constructing
                  typename match {
                    case "String" => Some("")
                    case "Int" => Some(0)
                    case "Long" => Some(0L)
                    case "Float" => Some(0.0F)
                    case "Double" => Some(0.0D)
                    case "Date" => Some(new java.util.Date(Long.MinValue))
                    case "Boolean" => Some(false)
                    case _ =>
                      Some(None)
                  }
                case mm =>
                  mm
              }

            }
        }
        result
      }

      def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = {
        val result = for {
          v <- getWitness(m, witness.value.name, typeable.describe , (
            typeable.describe match {
              case typeNameEx(x) => true
              case _ => false
            }))
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
}
