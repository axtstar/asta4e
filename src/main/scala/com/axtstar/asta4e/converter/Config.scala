package com.axtstar.asta4e.converter

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.core.{ExcelBasic, Location}

object Config {

  object DateReadParse {
    var ParserString: String = "EEE MMM dd HH:mm:ss Z yyyy"
    var Locale: String = "JST"
  }

  object DateWriteParse {
    var ParserString: String = "EEE MMM dd HH:mm:ss Z yyyy"
    var Locale: String = "GMT"
  }

}