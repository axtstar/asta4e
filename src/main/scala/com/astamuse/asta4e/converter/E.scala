package com.astamuse.asta4e.converter

import scala.language.implicitConversions

object E {
  implicit class ExcelConverter(s: (String, String)) {
    def &(that: (String, String)): List[(String, String)] = {
      s :: that :: Nil
    }
  }

  implicit class ExcelConverterList(s: List[(String, String)]) {
    def &(that: (String, String)): List[(String, String)] = {
      (that :: s).reverse
    }
  }
}
