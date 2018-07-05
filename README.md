[![Build Status](https://travis-ci.org/axtstar/asta4e.svg?branch=master)](https://travis-ci.org/axtstar/asta4e)

# asta4e

This library provide a template engine functionality for Excel for scala.

# feature

- retrieve data from Excel and convert them to Map[String, Any] or a case class

- create Excel from Map[String, Any] or a case class

# required

- scala 2.11
- scala 2.12

# usage

add dependency like the below

```sbt
libraryDependencies ++= Seq(
      "com.axtstar" %% "asta4e" % "0.0.2"
)
```

- get bind data from Excel
 
  The function needs 2 Excel files, first template Excel contains ${}. Second is data Excel file which contains data as exact same location as template Excel file.

```scala
val target = ExcelMapper.getDataAsTemplate(
        "template.xlsx",
        "data.xlsx")
```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  then, return to binddata to `target` 

- set bind data to Excel

  The function also needs 2 input Excel, first is Excel as same as the above, second is layout template Excel which copy to output data, asta4e does not manupilate excel layout.

```scala
import com.axtstar.asta4e.converter.E._
ExcelMapper.setDataAsTemplate(
        "template.xlsx",
        "data_template.xlsx",
        "output.xlsx",
        "A1" -> "test1" &
        "A2" -> null &
        "A3" -> "test3" &
        "A4" -> 1
)
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  output Excel. 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  `com.axtstar.asta4e.converter.E._` is commpanion utilites for implicit conversion and gethering separeted tuple to Map.

# LICENSE

```
Copyright 2018 axt

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
