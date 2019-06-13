package com.axtstar.asta4e.basic

import java.io.{FileInputStream, InputStreamReader, OutputStreamWriter}
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.axtstar.asta4e.converter.Config
import com.axtstar.asta4e.core.{DataCore, InitialCore}
import com.opencsv.{CSVParserBuilder, CSVReaderBuilder, CSVWriterBuilder, ICSVWriter}

object CsvBasic {

}

trait CsvBasic extends DataCore with InitialCore [CsvBasic] {
  protected var separator = ','
  protected var quoteChar = '"'
  protected var encoding = "UTF-8"

  def withEncoding(_encoding:String)={
    this.encoding = _encoding
    this
  }

  def getColumnSize(iStream: FileInputStream)={
    val parser = new CSVParserBuilder()
      .withSeparator(separator)
      .withQuoteChar(quoteChar)
      .build()
    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = new CSVReaderBuilder(inputStreamReader)
      .withCSVParser(parser)
      .build()
    try {

      val oneLine = reader.readNext()
      oneLine.size
    } catch {
      case ex:Throwable =>
        throw ex
    }
    finally {
      reader.close()
      inputStreamReader.close()
      iStream.close()
    }

  }

  def getRowSize(iStream: FileInputStream)={
    val parser = new CSVParserBuilder()
      .withSeparator(separator)
      .withQuoteChar(quoteChar)
      .build()
    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = new CSVReaderBuilder(inputStreamReader)
      .withCSVParser(parser)
      .build()
    try {

      val oneLine = reader.readAll()
      oneLine.size
    } catch {
      case ex:Throwable =>
        throw ex
    }
    finally {
      reader.close()
      inputStreamReader.close()
      iStream.close()
    }

  }


  override def _getData(iStream: FileInputStream): IndexedSeq[(String, Map[String, Any])] = {
    val parser = new CSVParserBuilder()
      .withSeparator(separator)
      .withQuoteChar(quoteChar)
      .build()
    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = new CSVReaderBuilder(inputStreamReader)
      .withCSVParser(parser)
      .build()

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
      case ex:Throwable =>
        throw ex
    } finally {
      reader.close()
      inputStreamReader.close()
      iStream.close()
    }
  }


  override def _getDataDown(iStream: FileInputStream): IndexedSeq[(String, IndexedSeq[Map[String, Any]])] = {
    val parser = new CSVParserBuilder()
      .withSeparator(separator)
      .withQuoteChar(quoteChar)
      .build()

    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = new CSVReaderBuilder(inputStreamReader)
      .withCSVParser(parser)
      .build()

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
      case ex:Throwable =>
        throw ex
    } finally {
      reader.close()
      inputStreamReader.close()
      iStream.close()
    }
  }

  override def _setData(bindData: (String, Map[String, Any])*): Unit = {

    val parser = new CSVParserBuilder()
      .withSeparator(separator)
      .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
      .build()

    val outputStreamWriter = new OutputStreamWriter(outputStream, encoding)
    val writer = new CSVWriterBuilder(outputStreamWriter)
      .withParser(parser)
      .build()

    try{
      bindData.map {
        x =>
          val map = x._2
          val m: Array[String] = Array.fill[String](
            1 + locationMap.map { f => f.positionX }.max
          )(""/*initial value*/)
          locationMap.foreach {
            l =>
              if (map.contains(l.name)) {
                m(l.positionX) = map(l.name) match {
                  case null => ""
                  case mm:Date =>
                    s"${quoteChar}${LocalDateTime.ofInstant(mm.toInstant, ZoneId.systemDefault()).format(Config.DateReadParse)}${quoteChar}"
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
      case ex:Throwable =>
        throw ex
    } finally {
      writer.close()
      outputStreamWriter.close()
      outputStream.close()
    }

  }

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*): Unit = {

    val parser = new CSVParserBuilder().withSeparator(separator)
      .withQuoteChar(ICSVWriter.NO_QUOTE_CHARACTER)
      .build()

    val outputStreamWriter = new OutputStreamWriter(outputStream, encoding)
    val writer = new CSVWriterBuilder(outputStreamWriter)
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
                        s"${quoteChar}${LocalDateTime.ofInstant(mm.toInstant, ZoneId.systemDefault()).format(Config.DateReadParse)}${quoteChar}"
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
      case ex:Throwable =>
        throw ex
    } finally {
      writer.close()
      outputStreamWriter.close()
      outputStream.close()
    }

  }

}
