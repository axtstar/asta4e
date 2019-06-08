package com.axtstar.asta4e.test_location

import com.axtstar.asta4e.etc.Location

object Location_4_CSV {
  val ao_a1_a2_startRow_as_0 = List[Location](
    Location.create("a0",0,0,List("${a0}")),
    Location.create("a1",1,0,List("${a1}")),
    Location.create("a2",2,0,List("${a2}"))
  )

  val ao_a1_a2_startRow_as_1 = List[Location](
    Location.create("a0",0,1,List("${a0}")),
    Location.create("a1",1,1,List("${a1}")),
    Location.create("a2",2,1,List("${a2}"))
  )

}

case class CSV_Data(
                   a0:String,
                   a1:String,
                   a2:String
)
