package com.axtstar.asta4e.core

import java.io.{FileInputStream, FileOutputStream}

trait InitialCore[A] {
  protected var ignoreSheets:List[String] = List()
  protected var layoutXls:FileInputStream = null
  protected var outputXls:FileOutputStream = null
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
    *  set sheet names which doesn't include at retrieving Excel
    * @param _ignoreSheets
    * @return ExcelBasic(this)
    */
  def withIgnoreSheets(_ignoreSheets:List[String])={
    this.ignoreSheets = _ignoreSheets
    this.asInstanceOf[A]
  }

  /**
    * set layout excel as FileInputStream at writing excel
    * @param _layoutXls
    * @return ExcelBasic(this)
    */
  def withLayoutXls(_layoutXls:FileInputStream)={
    this.layoutXls = _layoutXls
    this.asInstanceOf[A]
  }

  /**
    * set output excel file as FileOutputStream at writing excel
    * @param _outputXls
    * @return ExcelBasic(this)
    */
  def withOutXls(_outputXls:FileOutputStream)={
    this.outputXls = _outputXls
    this.asInstanceOf[A]
  }

}
