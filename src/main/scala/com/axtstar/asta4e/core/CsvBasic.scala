package com.axtstar.asta4e.core
import java.io.{FileInputStream, FileReader, FileWriter, InputStreamReader}

import com.opencsv.{CSVParserBuilder, CSVReaderBuilder, CSVWriterBuilder}

object CsvBasic {

}

trait CsvBasic extends DataCore[ExcelBasic] with InitialCore [CsvBasic] /*with DataCore[CsvBasic]*/ {
  protected var separator = ','
  protected var quoteChar = '"'

  override def _getData[B](iStream: FileInputStream)
                         (f: Map[String, Any] => B): IndexedSeq[(String, B)] = {
    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(quoteChar).build()
    val fileReader = new FileReader(iStream.getFD)
    val reader = new CSVReaderBuilder(fileReader)
      .withCSVParser(parser).build()

    val minRow = locationMap.map(_.positionY).min
    val maxRow = locationMap.map(_.positionY).max

    //val offSet = locationMap.groupBy(x=>x.positionY)

    try {
      reader.skip(minRow) // first skip
      val oneLine = reader.readNext()
      val map = (locationMap.map{
        x =>
          val target = oneLine(x.positionX).asInstanceOf[Any]
          x.name -> target
      }).toMap
      //TODO : "" as FileName
      IndexedSeq("" -> f(map))
    } catch {
      case ex:Exception =>
        throw ex
    } finally {
      reader.close()
      fileReader.close()
      iStream.close()
    }
  }


  override def _getDataDown[B](iStream: FileInputStream)
                (f: Map[String, Any] => B): IndexedSeq[(String, IndexedSeq[B])] = {
    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(quoteChar).build()
    val fileReader = new FileReader(iStream.getFD)
    val reader = new CSVReaderBuilder(fileReader)
      .withCSVParser(parser).build()

    val minRow = locationMap.map(_.positionY).min
    val maxRow = locationMap.map(_.positionY).max

    //val offSet = locationMap.groupBy(x=>x.positionY)

    try {
      reader.skip(minRow) // first skip
      val oneLines = reader.readAll()
      //TODO : "" as FileName
      IndexedSeq("" -> (for (i <- 0 until oneLines.size()) yield {
        val oneLine = oneLines.get(i)
        val map = (locationMap.map {
          x =>
            val target = oneLine(x.positionX)
            x.name -> target
        }).toMap
        f(map)
      }))
    } catch {
      case ex:Exception =>
        throw ex
    } finally {
      reader.close()
      fileReader.close()
      iStream.close()
    }
  }

  override def _setData(bindData: (String, Map[String, Any])*)(f: Map[String, Any] => Map[String, Any]): Unit = {
    val fileWriter = new FileWriter(outputStream.getFD)

    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(quoteChar).build()

    val writer = new CSVWriterBuilder(fileWriter)
      .withParser(parser).build()

    try{
      bindData.map {
        x =>
          val map = f(x._2).map{xx=>
            xx._2.toString()
          }.toArray
        writer.writeNext(map, false)
      }

    } catch {
      case ex:Exception =>
        throw ex
    } finally {
      writer.close()
      fileWriter.close()
      outputStream.close()
    }

  }

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*)(f: Map[String, Any] => Map[String, Any]): Unit = {
    val fileWriter = new FileWriter(outputStream.getFD)

    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(quoteChar).build()

    val writer = new CSVWriterBuilder(fileWriter)
      .withParser(parser).build()

    try{
      bindData.map {
        b =>
          val x = b._2
          x.map {
            xx =>
            val map = f(xx).map { xxx => xxx._2.toString }.toArray
            writer.writeNext(map)
          }
      }

    } catch {
      case ex:Exception =>
        throw ex
    } finally {
      writer.close()
      fileWriter.close()
      outputStream.close()
    }

  }

}
