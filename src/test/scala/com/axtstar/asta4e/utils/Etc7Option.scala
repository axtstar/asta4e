package com.axtstar.asta4e.utils

import java.util.Date

case class Etc7Option(
              numeric: Option[Double],
              string:Option[String],
              date:Option[Date],
              formula:Option[String],
              bool:Option[Boolean],
              time:Option[Date],
              userDate:Option[Date]
)
