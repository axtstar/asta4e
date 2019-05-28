package com.axtstar.asta4e.core
import java.io.{FileInputStream, FileReader, InputStreamReader}

import com.opencsv.{CSVParserBuilder, CSVReaderBuilder}

object CsvBasic {

}

trait CsvBasic extends InitialCore [CsvBasic] {
  protected var separator = ','
  protected var quoteChar = '"'

  def getData[B](iStream: FileInputStream)
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


  def getDataDown[B](iStream: FileInputStream)
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


}
