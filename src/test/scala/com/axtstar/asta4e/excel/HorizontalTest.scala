package com.axtstar.asta4e.excel

import java.io.File

import com.axtstar.asta4e._
import com.axtstar.asta4e.test_class.Data_HOLIZONTAL
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HorizontalTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "Horizontal" should {
    "Get" in {
      val target = ExcelMapper.by[Data_HOLIZONTAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx",
        List()
      )
      target.size must be_==(1)
      target(0)._2.get.A1 must be_==("A1")
      target(0)._2.get.Z1 must be_==("Z1")

    }

    "GetDown" in {
      val target = ExcelMapper.by[Data_HOLIZONTAL].getDataDown(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx",
        List()
      )
      target.size must be_==(1)
      target(0)._2(0).get.A1 must be_==("A1")
      target(0)._2(4).get.Z1 must be_==("Z5")

    }

    "Set" in {
      val dh0 = Data_HOLIZONTAL(A1="",
        B1="",
        C1="",
        D1="",
        E1="",
        F1="",
        G1="",
        H1="",
        I1="",
        J1="",
        K1="",
        L1="",
        M1="",
        N1="",
        O1="",
        P1="",
        Q1="",
        R1="",
        S1="",
        T1="",
        U1="",
        V1="",
        W1="",
        X1="",
        Y1="",
        Z1="",
        AA1="",
        AB1="",
        AC1="",
        AD1="",
        AE1="",
        AF1="",
        AG1="1AG"
      )
      val list = IndexedSeq("Sheet1" -> Option(dh0))
      ExcelMapper.by[Data_HOLIZONTAL].setData(
        /* バインド */
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        /* レイアウト */
        s"${currentDir}/src/test/resources/excel/output_white.xlsx",
        /* 出力先 */
        s"${currentDir}/target/output_horizontal_set.xlsx",
        list
      )

      val target = ExcelMapper.by[Data_HOLIZONTAL].getData(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/target/output_horizontal_set.xlsx",
        List()
      )

      target.size must be_==(1)
      target(0)._2.get.AG1 must be_==("1AG")

    }


    "SetDown" in {
      val dh0 = Data_HOLIZONTAL(A1="",
        B1="",
        C1="",
        D1="",
        E1="",
        F1="",
        G1="",
        H1="",
        I1="",
        J1="",
        K1="",
        L1="",
        M1="",
        N1="",
        O1="",
        P1="",
        Q1="",
        R1="",
        S1="",
        T1="",
        U1="",
        V1="",
        W1="",
        X1="",
        Y1="",
        Z1="",
        AA1="",
        AB1="",
        AC1="",
        AD1="",
        AE1="",
        AF1="",
        AG1="1AG"
      )

      val dh1 = Data_HOLIZONTAL(A1="",
        B1="",
        C1="",
        D1="",
        E1="1E",
        F1="",
        G1="",
        H1="",
        I1="",
        J1="",
        K1="",
        L1="",
        M1="",
        N1="",
        O1="",
        P1="",
        Q1="",
        R1="",
        S1="",
        T1="",
        U1="",
        V1="",
        W1="",
        X1="",
        Y1="",
        Z1="",
        AA1="",
        AB1="",
        AC1="",
        AD1="",
        AE1="",
        AF1="",
        AG1="1AG"
      )

      val dh2 = Data_HOLIZONTAL(A1="",
        B1="",
        C1="",
        D1="",
        E1="1E",
        F1="",
        G1="",
        H1="",
        I1="",
        J1="",
        K1="",
        L1="",
        M1="",
        N1="",
        O1="",
        P1="",
        Q1="",
        R1="",
        S1="",
        T1="",
        U1="",
        V1="",
        W1="",
        X1="",
        Y1="",
        Z1="",
        AA1="",
        AB1="",
        AC1="",
        AD1="",
        AE1="",
        AF1="",
        AG1="1AG"
      )


      val list = IndexedSeq("Sheet1" -> IndexedSeq(Option(dh0),Option(dh1),Option(dh2)))
      ExcelMapper.by[Data_HOLIZONTAL].setDataDown(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/src/test/resources/excel/output_white.xlsx",
        s"${currentDir}/target/output_horizontal_set.xlsx",
        list
      )

      val target = ExcelMapper.by[Data_HOLIZONTAL].getDataDown(
        s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx",
        s"${currentDir}/target/output_horizontal_set.xlsx",
        List()
      )

      target.size must be_==(1)
      target(0)._2(0).get.AG1 must be_==("1AG")

    }


  }

}
