[![Build Status](https://travis-ci.org/axtstar/asta4e.svg?branch=master)](https://travis-ci.org/axtstar/asta4e) [![Coverage Status](https://coveralls.io/repos/github/axtstar/asta4e/badge.svg?branch=master)](https://coveralls.io/github/axtstar/asta4e?branch=master)

# Asta4e

This library provide a template engine functionality for Excel for scala.

# Feature

- Retrieve data from Excel and convert them to Map[String, Any] or a case class

- Create Excel from Map[String, Any] or a case class

# Required

- scala 2.11 or
- scala 2.12

# Concept

 This library avoids a lot of boilerplate and location definitions.

 Ordinary, typical apache poi coding is like location definitions, and they likely make mistakes a lot.

 In order to avoid location definition in code, asta4e needs to prepare template Excel as location definition.
  
# Usage

add dependency like the below

```sbt
libraryDependencies ++= Seq(
      "com.axtstar" %% "asta4e" % "0.0.6"
)
```

- get bind data from Excel
 
  The function needs 2 Excel files, first template Excel contains ${}. Second is data Excel file which contains data as exact same location as template Excel file.

  Arbitrary given excel template must contains data binder such as ${numeric}.

```scala
val target = ExcelMapper.getData(
        "template.xlsx",
        "data.xlsx",
        List("ignoresheets"))
```

Excel template has two paramater, then you can create case class as same parameter.

```scala
case class Data(numeric:Double, string:String)
```

Then, get the data to the class from Excel like the below code. 

```scala
val data:Option[Data] = ExcelMapper.by[Data].getDataAsAny(
        "template.xlsx",
        "data.xlsx",
        List("ignoresheets"))
```


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  then, return to bind data to `target` 

- set bind data to Excel

  The function also needs 2 input Excel, first is Excel as same as the above, second is layout template Excel which copy to output data, asta4e does not manipulate excel layout.

```scala
import com.axtstar.asta4e.converter.E._
ExcelMapper.setData(
        "template.xlsx",
        "data_template.xlsx",
        "output.xlsx",
        "Sheet1" -> (
            "A1" -> "test1" &
            "A2" -> null &
            "A3" -> "test3" &
            "A4" -> 1
        )
)
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  output Excel. 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  `com.axtstar.asta4e.converter.E._` is companion utilites for implicit conversion and gethering separeted tuple to Map.

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
