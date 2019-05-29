package com.axtstar.asta4e.core

import java.io.FileInputStream

trait DataCore {
  def _setData(bindData: (String, Map[String, Any])*):Unit

  def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*):Unit

  def _getData(iStream: FileInputStream):IndexedSeq[(String,Map[String, Any])]

  def _getDataDown(iStream:FileInputStream):IndexedSeq[(String, IndexedSeq[Map[String, Any]])]
}
