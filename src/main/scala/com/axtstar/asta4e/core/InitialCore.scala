package com.axtstar.asta4e.core

import java.io.{FileInputStream, FileOutputStream}

import com.axtstar.asta4e.basic.ExcelBasic

trait InitialCore[A] {
  protected var ignoreSheets:List[String] = List()
  protected var layoutStram:FileInputStream = null
  protected var outputStream:FileOutputStream = null
  protected var locationMap : List[Location] = List()

  def withLocation(_locationMap:List[Location])
                  (implicit ev:A  <:< InitialCore[A]) ={
    this.locationMap= _locationMap
    this.asInstanceOf[A]
  }

  def withLocation(_locationMapPathExcel: String) = {
    this.locationMap = ExcelBasic.getExcelLocation(_locationMapPathExcel)
    this.asInstanceOf[A]
  }

  /**
    * set output excel file as FileOutputStream at writing excel
    * @param _outputStream
    * @return ExcelBasic(this)
    */
  def withOutStream(_outputStream:FileOutputStream)={
    this.outputStream = _outputStream
    this.asInstanceOf[A]
  }

  def withOutStream(_outputPath:String)={
    this.outputStream = new FileOutputStream(_outputPath)
    this.asInstanceOf[A]
  }

}
