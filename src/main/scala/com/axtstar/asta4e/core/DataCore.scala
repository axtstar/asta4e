package com.axtstar.asta4e.core

import java.io.FileInputStream

trait DataCore[A] {
  def _setData(bindData: (String, Map[String, Any])*)
             (f:Map[String, Any] => Map[String, Any]):Unit

  def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*)
                 (f:Map[String, Any] => Map[String, Any]):Unit

  def _getData[B](
                  iStream: FileInputStream
                )(f:Map[String, Any] => B):IndexedSeq[(String,B)]

  def _getDataDown[B](iStream:FileInputStream)
                    (f:Map[String, Any] => B):IndexedSeq[(String, IndexedSeq[B])]
}
