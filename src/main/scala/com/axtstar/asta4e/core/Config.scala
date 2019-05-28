package com.axtstar.asta4e.core

import java.io.{FileInputStream, FileOutputStream}

trait Config {
  var ignoreSheets:List[String] = List()
  var layoutStram:FileInputStream = null
  var outputStream:FileOutputStream = null
  var locationMap : List[Location] = List()
  var a:Any = null

  def builder[A](_a:A)={
    a = _a
    this
  }

  def withLocation(_locationMap: List[Location]) = {
    this.locationMap = _locationMap
    this
  }

  def withLocation(_locationMapPathExcel: String) = {
    this.locationMap = ExcelBasic.getExcelLocation(_locationMapPathExcel)
    this
  }

  def withIgnoreSheets(_ignoreSheets: List[String]) = {
    this.ignoreSheets = _ignoreSheets
    this
  }

  def withLayoutXls(_layoutXls: FileInputStream) = {
    this.layoutStram = _layoutXls
    this
  }

  def withLayoutXls(_layoutXlsPath: String) = {
    this.layoutStram = (new FileInputStream(_layoutXlsPath))
    this
  }

  def withOutStream(_outputXls: FileOutputStream) = {
    this.outputStream = _outputXls
    this
  }

  def withOutStream(_outputXlsPath: String) = {
    this.outputStream = (new FileOutputStream(_outputXlsPath))
    this
  }

  def bind[A]()={
    a.asInstanceOf[A]
  }
}
