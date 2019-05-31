package com.axtstar.asta4e.converter

import java.io.{FileInputStream, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.Locale

import com.axtstar.asta4e.core.Location

object Config {

  var DateReadParse = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("GMT"))
}