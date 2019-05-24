package com.axtstar.asta4e

import java.io.File

import com.axtstar.asta4e.test_class.{Data_HOLIZONTAL, Data_VIRTICAL}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class VerticalTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "Vertical" should {
    "Get" in {
      val target = ExcelMapper.by[Data_VIRTICAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/src/test/resources/excel/read_vertical.xlsx",
        List()
      )
      target.size must be_==(1)
      target(0)._2.get.A1 must be_==("${A1}")
      target(0)._2.get.A26 must be_==("${A26}")

    }

    "GetDown" in {
      val target = ExcelMapper.by[Data_VIRTICAL].getDataDown(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/src/test/resources/excel/read_vertical.xlsx",
        List()
      )
      target.size must be_==(1)
      target(0)._2(0).get.A1 must be_==("${A1}")
      target(0)._2(0).get.A26 must be_==("${A26}")

    }

    "Set" in {
      val dh0 = Data_VIRTICAL(A1="",
        A2="",
        A3="",
        A4="",
        A5="",
        A6="",
        A7="",
        A8="",
        A9="",
        A10="",
        A11="",
        A12="",
        A13="",
        A14="",
        A15="",
        A16="",
        A17="",
        A18="",
        A19="",
        A20="",
        A21="",
        A22="",
        A23="",
        A24="",
        A25="",
        A26="",
        A27="",
        A28="",
        A29="",
        A30="",
        A31="",
        A32="23A",
        A33=""
      )
      val list = IndexedSeq("Sheet1" -> Option(dh0))
      ExcelMapper.by[Data_VIRTICAL].setData(
        /* バインド */
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        /* レイアウト */
        s"${currentDir}/src/test/resources/excel/output_white.xlsx",
        /* 出力先 */
        s"${currentDir}/target/output_vertical_set.xlsx",
        list
      )

      val target = ExcelMapper.by[Data_VIRTICAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/target/output_vertical_set.xlsx",
        List()
      )

      target.size must be_==(1)
      target(0)._2.get.A32 must be_==("23A")

    }


    "SetDown" in {
      val dh0 = Data_VIRTICAL(A1="1A",
        A2="",
        A3="",
        A4="",
        A5="",
        A6="",
        A7="",
        A8="",
        A9="",
        A10="",
        A11="",
        A12="",
        A13="",
        A14="",
        A15="",
        A16="",
        A17="",
        A18="",
        A19="",
        A20="",
        A21="",
        A22="",
        A23="",
        A24="",
        A25="",
        A26="",
        A27="",
        A28="",
        A29="",
        A30="",
        A31="",
        A32="23A",
        A33=""
      )

      val dh1 = Data_VIRTICAL(A1="",
        A2="",
        A3="",
        A4="",
        A5="",
        A6="",
        A7="",
        A8="",
        A9="",
        A10="",
        A11="",
        A12="",
        A13="",
        A14="",
        A15="",
        A16="",
        A17="",
        A18="",
        A19="",
        A20="",
        A21="",
        A22="",
        A23="",
        A24="",
        A25="",
        A26="",
        A27="",
        A28="",
        A29="",
        A30="",
        A31="",
        A32="23A",
        A33=""
      )

      val dh2 = Data_VIRTICAL(A1="",
        A2="",
        A3="",
        A4="",
        A5="",
        A6="",
        A7="",
        A8="",
        A9="",
        A10="",
        A11="",
        A12="",
        A13="",
        A14="",
        A15="",
        A16="",
        A17="",
        A18="",
        A19="",
        A20="",
        A21="",
        A22="",
        A23="",
        A24="",
        A25="",
        A26="",
        A27="",
        A28="",
        A29="29A",
        A30="",
        A31="",
        A32="23A",
        A33=""
      )


      val list = IndexedSeq("Sheet1" -> IndexedSeq(Option(dh0),Option(dh1),Option(dh2)))
      ExcelMapper.by[Data_VIRTICAL].setDataDown(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/src/test/resources/excel/output_white.xlsx",
        s"${currentDir}/target/output_vertical_setdown.xlsx",
        list
      )

      val target = ExcelMapper.by[Data_VIRTICAL].getDataDown(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/target/output_vertical_setdown.xlsx",
        List()
      )

      target.size must be_==(1)
      target(0)._2(0).get.A1 must be_==("1A")
      target(0)._2(2).get.A29 must be_==("29A")

    }


  }

}
