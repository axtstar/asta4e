package com.axtstar.asta4e.excel

import java.io.File

import com.axtstar.asta4e.ExcelMapper
import com.axtstar.asta4e.test_class.Data_VIRTICAL
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
      ExcelMapper.by[Data_VIRTICAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx")
        .withLayoutXls(s"${currentDir}/src/test/resources/excel/output_white.xlsx")
        .withOutXls(s"${currentDir}/target/output_vertical_set.xlsx")
        .setCC(list)

      val target = ExcelMapper.by[Data_VIRTICAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_vertical.xlsx",
        s"${currentDir}/target/output_vertical_set.xlsx",
        List()
      )

      target.size must be_==(1)
      target(0)._2.get.A32 must be_==("23A")

    }


    "SetDown" in {
      val dh0 = Data_VIRTICAL(
        A1="A1_0",
        A2="A2_0",
        A3="A3_0",
        A4="A4_0",
        A5="A5_0",
        A6="A6_0",
        A7="A7_0",
        A8="A8_0",
        A9="A9_0",
        A10="A10_0",
        A11="A11_0",
        A12="A12_0",
        A13="A13_0",
        A14="A14_0",
        A15="A15_0",
        A16="A16_0",
        A17="A17_0",
        A18="A18_0",
        A19="A19_0",
        A20="A20_0",
        A21="A21_0",
        A22="A22_0",
        A23="A23_0",
        A24="A24_0",
        A25="A25_0",
        A26="A26_0",
        A27="A27_0",
        A28="A28_0",
        A29="A29_0",
        A30="A30_0",
        A31="A31_0",
        A32="A32_0",
        A33="A33_0"
      )

      val dh1 = Data_VIRTICAL(
        A1="A1_1",
        A2="A2_1",
        A3="A3_1",
        A4="A4_1",
        A5="A5_1",
        A6="A6_1",
        A7="A7_1",
        A8="A8_1",
        A9="A9_1",
        A10="A10_1",
        A11="A11_1",
        A12="A12_1",
        A13="A13_1",
        A14="A14_1",
        A15="A15_1",
        A16="A16_1",
        A17="A17_1",
        A18="A18_1",
        A19="A19_1",
        A20="A20_1",
        A21="A21_1",
        A22="A22_1",
        A23="A23_1",
        A24="A24_1",
        A25="A25_1",
        A26="A26_1",
        A27="A27_1",
        A28="A28_1",
        A29="A29_1",
        A30="A30_1",
        A31="A31_1",
        A32="A32_1",
        A33="A33_1"
      )

      val dh2 = Data_VIRTICAL(
        A1="A1_2",
        A2="A2_2",
        A3="A3_2",
        A4="A4_2",
        A5="A5_2",
        A6="A6_2",
        A7="A7_2",
        A8="A8_2",
        A9="A9_2",
        A10="A10_2",
        A11="A11_2",
        A12="A12_2",
        A13="A13_2",
        A14="A14_2",
        A15="A15_2",
        A16="A16_2",
        A17="A17_2",
        A18="A18_2",
        A19="A19_2",
        A20="A20_2",
        A21="A21_2",
        A22="A22_2",
        A23="A23_2",
        A24="A24_2",
        A25="A25_2",
        A26="A26_2",
        A27="A27_2",
        A28="A28_2",
        A29="A29_2",
        A30="A30_2",
        A31="A31_2",
        A32="A32_2",
        A33="A33_2"
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
      target(0)._2(0).get.A1 must be_==("A1_0")
      target(0)._2(2).get.A29 must be_==("A29_2")

    }


  }

}
