package com.axtstar.asta4e.converter

import java.time.{LocalDate, ZoneId}
import java.util.Date

import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Poly0, Poly1, Typeable, Witness, ops}
import shapeless.labelled.{FieldType, field}

import scala.util.Try

object CC {

  implicit class By[T](val a: T) extends AnyVal {

    import ops.record._

    def toMap[L <: HList](implicit
                          gen: LabelledGeneric.Aux[T, L],
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


      private def isValidShort(data:Any)={
        data match{
          case x:Short =>
            true
          case x:Int =>
            if(x.isValidShort)x.toShort else 0
          case x:Long =>
            if(x.isValidShort)x.toShort else 0
          case x:Float =>
            if(x.isValidShort)x.toShort else 0
          case x:Double =>
            if(x.isValidShort)x.toShort else 0
          case x:Byte =>
            if(x.isValidShort)x.toShort else 0
          case x:BigInt =>
            if(x.isValidShort)x.toShort else 0
          case x:BigDecimal =>
            if(x.isValidShort)x.toShort else 0

        }

      }

      private def primitiveConverter[TV](m:Map[String, Any],name:String, typename:String)
                                        (implicit
                                         evidenceV: TV <:< AnyVal = null
                                        ) ={
        m.get(name) match {
          case Some(x) =>
            //if not determine from data, asta4e convert them to wrapped primitive value
            // TODO : need more precise way of constructing
            typename match {
              case "Char" =>
                Some(x match {
                  case xx:Char =>
                    xx
                  case xx:Short =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:Int =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:Long =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:Float =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:Double =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:BigInt =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:BigDecimal =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:Byte =>
                    if(xx.isValidChar)xx.toChar else Char.MinValue
                  case xx:String =>
                    if(xx==null){
                      Char.MinValue
                    } else if(xx.size >0){
                      val target = xx.charAt(0)
                      target
                    }
                    else {
                      Char.MinValue
                    }
                  case _ =>
                    Char.MinValue
                })
              case "String" =>
                x match {
                  case null =>
                    Some("")
                  case _ =>
                    Some(x.toString)
                }
              case "Short" =>
                Some(x match {
                  case xx:Short =>
                    xx
                  case xx:Int =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:Long =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:Float =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:Double =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:BigInt =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:BigDecimal =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:Byte =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:Char =>
                    if(xx.isValidShort)xx.toShort else 0
                  case xx:String =>
                    Try(xx.toShort).getOrElse(0)
                  case _ =>
                    0
                })
              case "Int" =>
                Some(x match {
                  case xx:Int =>
                      xx
                  case xx:Short =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:Long =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:Float =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:Double =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:BigInt =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:BigDecimal =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:Byte =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:Char =>
                    if(xx.isValidInt)xx.toInt else 0
                  case xx:String =>
                    Try(xx.toInt).getOrElse(0)
                  case _ =>
                    0
                })
              case "Long" =>
                Some(x match {
                  case xx:Short =>
                    if(xx.isValidLong)xx.toLong else 0
                  case xx:Int =>
                    if(xx.isValidLong)xx.toLong else 0
                  case xx:Long =>
                    xx
                  case xx:Float =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:Double =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:BigInt =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:BigDecimal =>
                    Try(xx.toLong).getOrElse(0L)
                  case xx:Byte =>
                    if(xx.isValidLong)xx.toLong else 0
                  case xx:Char =>
                    if(xx.isValidLong)xx.toLong else 0
                  case xx:String =>
                    Try(xx.toLong).getOrElse(0L)
                  case _ =>
                    Try(x.toString.toLong).getOrElse(0L)
                })
              case "Float" =>
                Some(x match {
                  case xx:Short =>
                    xx.toFloat
                  case xx:Int =>
                    xx.toFloat
                  case xx:Long =>
                    xx.toFloat
                  case xx:Float =>
                    xx
                  case xx:Double =>
                    xx.toFloat
                  case xx:BigInt =>
                    if(xx.isValidFloat) xx.toFloat else 0
                  case xx:BigDecimal =>
                    if(xx.isExactFloat) xx.toFloat else 0
                  case xx:Byte =>
                    xx.toFloat
                  case xx:Char =>
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
                  case xx:Short =>
                    xx.toDouble
                  case xx:Long =>
                    xx.toDouble
                  case xx:BigInt =>
                    if(xx.isValidDouble) xx.toDouble else 0
                  case xx:BigDecimal =>
                    if(xx.isExactDouble) xx.toDouble else 0
                  case xx:Byte =>
                    xx.toDouble
                  case xx:Char =>
                    xx.toDouble
                  case _ =>
                    Try(x.toString.toDouble).getOrElse(0D)
                })
              case "Byte" =>
                Some(x match {
                  case xx:Byte =>
                    xx
                  case xx:Short =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:Int =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:Long =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:Float =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:Double =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:BigInt =>
                    if(xx.isValidByte) xx.toByte else 0
                  case xx:BigDecimal =>
                    if(xx.isValidByte) xx.toByte else 0
                  case xx:Date =>
                    if(xx.getTime.isValidByte)xx.getTime.toByte else 0
                  case xx:Char =>
                    if(xx.isValidByte)xx.toByte else 0
                  case xx:String =>
                    Try(xx.toByte).getOrElse(0)
                  case _ =>
                    0
                })
              case "Date" =>
                Some(x match {
                  case xx:Short =>
                    new Date(xx.toLong)
                  case xx:Int =>
                    new Date(xx.toLong)
                  case xx:Long =>
                    new Date(xx)
                  case xx:Char =>
                    new Date(xx.toLong)
                  case xx:Byte =>
                    new Date(xx.toLong)
                  case xx:Float =>
                    Try(new Date(xx.toLong)).getOrElse(new Date(Long.MinValue))
                  case xx:Double =>
                    Try(new Date(xx.toLong)).getOrElse(new Date(Long.MinValue))
                  case xx:Date =>
                    xx
                  case xx:String =>
                    Try({
                      val localDate = LocalDate.parse(xx, Config.DateReadParse)
                      Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    }).getOrElse(new Date(Long.MinValue))
                  case _ =>
                    new Date(Long.MinValue)
                })

              case "Boolean" =>
                Some(x match {
                  case xx:Boolean =>
                    xx
                  case xx:Short =>
                    x==0
                  case xx:Int =>
                    x==0
                  case xx:Long =>
                    xx==0L
                  case xx:Float =>
                    xx==0F
                  case xx:Double =>
                    xx==0D
                  case xx:BigInt =>
                    xx==(0:BigInt)
                  case xx:BigDecimal =>
                    xx==(0:BigDecimal)
                  case xx:Char =>
                    x==0
                  case xx:Byte =>
                    x==0
                  case xx:String =>
                    xx==""
                  case _ =>
                    false
                })
              case _ if (evidenceV!=null) =>
                Some(x)
              case _ =>
                Some(x)
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
  }

  object typeablePoly extends Poly1 {
    implicit def default[A](implicit typeable: Typeable[A]): Case.Aux[A, String] = at(_ => typeable.describe)
  }

  object nullPoly extends Poly0 {
    implicit def default[A]: ProductCase.Aux[HNil, A] = at(null.asInstanceOf[A])
  }

}
