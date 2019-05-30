[![Build Status](https://travis-ci.org/axtstar/asta4e.svg?branch=master)](https://travis-ci.org/axtstar/asta4e) [![Coverage Status](https://coveralls.io/repos/github/axtstar/asta4e/badge.svg?branch=master)](https://coveralls.io/github/axtstar/asta4e?branch=master)

# Asta4e

This library provide a template engine functionality for Excel for scala via case class.

# Feature
- Retrieve data from Excel and convert them to case class.

- Create Excel from case class.

- A case class to another case class converter.

# Required
- scala 2.11 or
- scala 2.12

# Motivation
   This library avoids a lot of boilerplate and location definitions powered by shapeless.

   Ordinary, typical apache poi coding is like location definitions, and they likely make mistakes a lot.

   In order to avoid location definition in code, asta4e needs a case class and a bind Excel as location definition.

   Then you can read excel as a case class way.
  
# Usage

add dependency like the below.

```sbt
libraryDependencies ++= Seq(
      "com.axtstar" %% "asta4e" % "0.0.10"
)
```

import library.
```scala
import com.axtstar.asta4e._
```

provide case class you want to get.
```scala
case class Data(
  string:String,
  int:Int,
  intOpt:Option[Int],
  date:Date
)
```

create excel as location definition.

location definition needs the dollar-brancket parameteres like the below.

${string}

${int}

${intOpt}

${date}

template.xlsx


you may reduce a definition from the definitions. so asta4e provides initial(zero) value, which currently cannot provide default value such as `int:Int=10`.

or you may add some another definiton(s) not including in the case class. in that case, asta4e just ignore them.

${another}

- get bind data from Excel

```scala
val target = ExcelMapper.by[Data]
        .withLocation("template.xlsx")
        .getCC(new FileInputStream("data.xlsx"))
```

then, return to bind data to `target` as Data type along side with sheetName.

```
IndexedSeq("sheetName" -> Data)

```

- set bind data to Excel

  The function also needs 2 input Excel, first is Excel as same as the above, second is layout template Excel which copy to output data, asta4e does not manipulate excel layout.

```scala
import com.axtstar.asta4e._
ExcelMapper[Data].setData(
        "template.xlsx",
        "data_template.xlsx",
        "output.xlsx",
        data
)
```

then, you get output.xlsx as output excel.

if you are interested in the library, check my gitter8 repos from the below command.

```bash
sbt new axtstar/asta4e-sample.g8
```

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
