package com.axtstar.asta4e.core
import java.io.{FileInputStream, FileReader, FileWriter, InputStreamReader}
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import com.axtstar.asta4e.converter.Config
import com.opencsv.{CSVParserBuilder, CSVReaderBuilder, CSVWriterBuilder, ICSVWriter}

object CsvBasic {

}

trait CsvBasic extends DataCore with InitialCore [CsvBasic] /*with DataCore[CsvBasic]*/ {
  protected var separator = ','
  protected var quoteChar = '"'

  override def _getData(iStream: FileInputStream): IndexedSeq[(String, Map[String, Any])] = {
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
      IndexedSeq("" ->
        map
      )
    } catch {
      case ex:Exception =>
        throw ex
    } finally {
      reader.close()
      fileReader.close()
      iStream.close()
    }
  }


  override def _getDataDown(iStream: FileInputStream): IndexedSeq[(String, IndexedSeq[Map[String, Any]])] = {
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
        map
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

  override def _setData(bindData: (String, Map[String, Any])*): Unit = {
    val fileWriter = new FileWriter(outputStream.getFD)

    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
      .build()

    val writer = new CSVWriterBuilder(fileWriter)
      .withParser(parser).build()

    try{
      bindData.map {
        x =>
          val map = x._2
          val m: Array[String] = Array.fill[String](1 + locationMap.map { f => f.positionX }.max)("")
          locationMap.foreach {
            l =>
              if (map.contains(l.name)) {
                m(l.positionX) = map(l.name) match {
                  case null => ""
                  case mm:Date =>
                    s"${quoteChar}${map(l.name).toString}${quoteChar}"
                  case mm:String =>
                    s"${quoteChar}${map(l.name).toString}${quoteChar}"
                  case _ =>
                    map(l.name).toString
                }
              }
          }
          writer.writeNext(m, false)
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

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*): Unit = {
    val fileWriter = new FileWriter(outputStream.getFD)

    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
      .build()

    val writer = new CSVWriterBuilder(fileWriter)
      .withParser(parser).build()

    try{
      bindData.map {
        b =>
          val x = b._2
          x.map {
            xx =>
              val map = xx
              val m:Array[String] = Array.fill[String](1 + locationMap.map{f => f.positionX}.max)("")
              locationMap.foreach {
                l =>
                  if(map.contains(l.name)) {
                    m(l.positionX) = map(l.name) match {
                      case null => ""
                      case mm:Date =>
                        val simpleDateFormat = new SimpleDateFormat(Config.DateWriteParse.ParserString, new Locale(Config.DateWriteParse.Locale))
                        s"${quoteChar}${simpleDateFormat.format(map(l.name))}${quoteChar}"
                      case mm:String =>
                        s"${quoteChar}${map(l.name).toString}${quoteChar}"
                      case _ =>
                        map(l.name).toString
                    }
                  }
              }
              writer.writeNext(m, false)
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
