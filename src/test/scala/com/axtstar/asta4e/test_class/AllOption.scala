package com.axtstar.asta4e.test_class

import java.util.Date

case class AllOption(
              numeric1: Option[String],
              numeric2: Option[String],
              numeric3: Option[String]
) {
  def getNum1 =numeric1.get.toDouble
  def getNum2 =numeric2.get.toDouble
  def getNum3 =numeric3.get.toDouble
}
