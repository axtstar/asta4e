package com.axtstar.asta4e.converter

import java.util.Date

import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Typeable, Witness}
import shapeless.labelled.{FieldType, field}

import scala.util.Try

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

      private def primitiveConverter(m:Map[String, Any],name:String, typename:String)={
        m.get(name) match {
          case Some(x) =>
            //if not determine from data, asta4e convert them to wrapped primitive value
            // TODO : need more precise way of constructing
            typename match {
              case "String" =>
                x match {
                  case null =>
                    Some("")
                  case _ =>
                    Some(x.toString)
                }
              case "Int" =>
                Some(x match {
                  case xx:Int =>
                    if(xx.isValidInt) {
                      xx.toInt
                    } else {
                      0
                    }
                  case xx:Long =>
                    if(xx.isValidInt) {
                      xx.toInt
                    } else {
                      0
                    }
                  case xx:Float =>
                    if(xx.isValidInt) {
                      xx.toInt
                    } else {
                      0
                    }
                  case xx:Double =>
                    if(xx.isValidInt) {
                      xx.toInt
                    } else {
                      0
                    }
                  case xx:String =>
                    Try(xx.toInt).getOrElse(0)
                  case _ =>
                    0
                })
              case "Long" =>
                Some(x match {
                  case xx:Long =>
                    xx
                  case xx:Int =>
                    xx.toLong
                  case xx:Double =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:Float =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:String =>
                    Try(xx.toLong).getOrElse(0L)
                  case _ =>
                    Try(x.toString.toLong).getOrElse(0L)
                })
              case "Float" =>
                Some(x match {
                  case xx:Float =>
                    xx
                  case xx:Double =>
                    xx.toFloat
                  case xx:Int =>
                    xx.toFloat
                  case xx:Long =>
                    xx.toFloat
                  case _ =>
                    Try(x.toString.toFloat).getOrElse(0F)
                })
              case "Double" =>
                Some(x match {
                  case xx:Double =>
                    xx
                  case xx:Float =>
                    xx.toDouble
                  case xx:Int =>
                    xx.toDouble
                  case xx:Long =>
                    xx.toDouble
                  case _ =>
                    Try(x.toString.toDouble).getOrElse(0D)
                })
              case "Date" =>
                Some(x match {
                  case xx:Int =>
                    new Date(xx.toLong)
                  case xx:Long =>
                    new Date(xx)
                  case xx:Float =>
                    Try(new Date(xx.toLong)).getOrElse(new Date(Long.MinValue))
                  case xx:Double =>
                    Try(new Date(xx.toLong)).getOrElse(new Date(Long.MinValue))
                  case xx:Date =>
                    xx
                  case xx:String =>
                    Date.parse(xx)
                  case _ =>
                    new Date(Long.MinValue)
                })

              case "Boolean" =>
                Some(x match {
                  case xx:Boolean =>
                    xx
                  case xx:Int =>
                    x==0
                  case xx:Long =>
                    xx==0L
                  case xx:Float =>
                    xx==0F
                  case xx:Double =>
                    xx==0D
                  case xx:String =>
                    xx==""
                  case _ =>
                    false
                })
              case _ =>
                Some(None)
            }
          case mm =>
            mm
        }
      }

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
                  Option(primitiveConverter(m, name, typename))
              }

            } else {
              //primitive class
              primitiveConverter(m, name, typename)

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
          h <- {
            val result = typeable.cast(v)
            result
          }
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
