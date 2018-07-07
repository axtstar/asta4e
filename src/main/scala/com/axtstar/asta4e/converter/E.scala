package com.axtstar.asta4e.converter

import scala.language.implicitConversions

object E {
  implicit class TupleMap(s: (String, Any)) {
    def &(that: (String, Any)): Map[String, Any] = {
      Map(s,that)
    }
  }

  implicit class MapMap(s: Map[String, Any]) {
    def &(that: (String, Any)): Map[String, Any] = {
      Map(that) ++ s
    }
  }

  implicit class TupleArray(s: (String,Map[String, Any])) {
    def &(that: (String,Map[String, Any])): Array[(String,Map[String, Any])] = {
      Array(s,that)
    }
  }

  implicit class ArrayTuple(s: Array[(String,Map[String, Any])]) {
    def &(that: (String,Map[String, Any])): Array[(String,Map[String, Any])] = {
      s ++ Array(that)
    }
  }

  implicit class ArrayArray(s: Array[(String,Map[String, Any])]) {
    def &(that: Array[(String,Map[String, Any])]): Array[(String,Map[String, Any])] = {
      that ++ s
    }
  }

}
