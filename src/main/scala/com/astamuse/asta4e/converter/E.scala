package com.astamuse.asta4e.converter

import scala.language.implicitConversions

object E {
  implicit class ExcelConverter(s: (String, Any)) {
    def &(that: (String, Any)): Map[String, Any] = {
      Map(s,that)
    }
  }

  implicit class ExcelConverterList(s: Map[String, Any]) {
    def &(that: (String, Any)): Map[String, Any] = {
      Map(that) ++ s
    }
  }
}
