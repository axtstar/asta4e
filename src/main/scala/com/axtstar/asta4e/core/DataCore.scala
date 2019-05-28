package com.axtstar.asta4e.core

import java.io.FileInputStream

trait DataCore[A] {
  def setData(bindData: (String, Map[String, Any])*)
             (f:Map[String, Any] => Map[String, Any]):Unit

  def setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*)
                 (f:Map[String, Any] => Map[String, Any]):Unit

  def getData[B](
                  iStream: FileInputStream
                )(f:Map[String, Any] => B):IndexedSeq[(String,B)]

  def getDataDown[B](iStream:FileInputStream)
                    (f:Map[String, Any] => B):IndexedSeq[(String, IndexedSeq[B])]
}
