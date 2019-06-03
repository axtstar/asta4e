package com.axtstar.asta4e.excel

import java.io.{File, FileInputStream, FileOutputStream}

import com.axtstar.asta4e._
import com.axtstar.asta4e.converter.{CC, MapHelper}
import com.axtstar.asta4e.test_class.Data_HOLIZONTAL
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HorizontalTest extends Specification {
  val currentDir = new File(".").getAbsoluteFile().getParent()

  "Horizontal" should {
    "Get" in {
      val target = ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        .getCC(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx"))

      target.size must be_==(1)
      target(0)._2.get.A1 must be_==("A1")
      target(0)._2.get.Z1 must be_==("Z1")

    }

    "GetDown" in {
      val target = ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        .getCCDown(new FileInputStream(s"${currentDir}/src/test/resources/excel/read_horizontal.xlsx"))

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

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","output_horizontal_set.csv")

      val list = IndexedSeq("Sheet1" -> Option(dh0))
      ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        .withLayoutXls(s"${currentDir}/src/test/resources/excel/output_white.xlsx")
        .withOutXls(ff.getAbsolutePath)
        .setCC(list)

      val target = ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        .getCC(new FileInputStream(ff.getAbsolutePath))

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

      val ff = java.io.File.createTempFile(s"${currentDir}/target/","output_horizontal_set.csv")

      val list = ("Sheet1" -> IndexedSeq(CC.By(dh0).toMap,CC.By(dh1).toMap,CC.By(dh2).toMap))
      ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        .withLayoutXls(s"${currentDir}/src/test/resources/excel/output_white.xlsx")
        .withOutStream(new FileOutputStream(ff.getAbsolutePath))
        ._setDataDown(list)

      val target = ExcelMapper.by[Data_HOLIZONTAL]
        .withLocation(s"${currentDir}/src/test/resources/excel/bind_horizontal.xlsx")
        ._getDataDown(new FileInputStream(ff.getAbsolutePath))

      target.size must be_==(1)
      val target2 = MapHelper.to[Data_HOLIZONTAL].from(target.head._2.head)
      target2.AG1 must be_==("1AG")

    }


  }

}
