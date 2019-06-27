package com.axtstar.asta4e.basic

import java.io.{FileInputStream, InputStreamReader, OutputStreamWriter}
import java.time.{LocalDateTime, ZoneId}
import java.util.Arrays
import java.util.Date

import com.axtstar.asta4e.converter.Config
import com.axtstar.asta4e.core.{DataCore, InitialCore}
import org.apache.commons.csv.{CSVFormat, QuoteMode}


object CsvBasic {

}

trait CsvBasic extends DataCore with InitialCore [CsvBasic] {
  protected var delimiter = ','
  protected var separator = '\n'
  protected var quoteChar = '"'
  protected var encoding = "UTF-8"
  protected var quoteMode:QuoteMode = QuoteMode.ALL

  private def getFormatter:CSVFormat= {
    CSVFormat.DEFAULT
      .withDelimiter(delimiter)
      .withRecordSeparator(separator)
      .withEscape('\\')
      .withQuote(quoteChar)
      .withQuoteMode(quoteMode)
  }

  def withDelimiter(_delimiter:Char):CsvBasic={
    this.delimiter = _delimiter
    this
  }

  def withSeparator(_separator:Char):CsvBasic={
    this.separator = _separator
    this
  }

  def withQuoteMode(_quoteMode:QuoteMode):CsvBasic={
    this.quoteMode = _quoteMode
    this
  }

  def withEncoding(_encoding:String):CsvBasic={
    this.encoding = _encoding
    this
  }

  def getColumnSize(iStream: FileInputStream):Int={
    val parser = getFormatter

    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val readers = parser.parse(inputStreamReader)

    try {

      val oneLine = readers.getRecords.get(0).size()
      oneLine
    } catch {
      case ex:Throwable =>
        throw ex
    }
    finally {
      readers.close()
      inputStreamReader.close()
      iStream.close()
    }

  }

  def getRowSize(iStream: FileInputStream):Int={
    val parser = getFormatter

    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = parser.parse(inputStreamReader)
    try {
      reader.getRecords.size()
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
    val parser = getFormatter

    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = parser.parse(inputStreamReader)

    val minRow = locationMap.map(_.positionY).min

    //val offSet = locationMap.groupBy(x=>x.positionY)

    try {
      val oneLine = reader.getRecords.get(minRow)

      val map = locationMap.map{
        x =>
          val target = if(oneLine.size() > x.positionX)
          {
            oneLine.get(x.positionX).asInstanceOf[Any]
          } else {
            null
          }
          x.name -> target
      }.toMap
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
    val parser = getFormatter

    val inputStreamReader = new InputStreamReader(iStream, encoding)
    val reader = parser.parse(inputStreamReader)

    val oneLines = reader.getRecords
    val minRow = locationMap.map(_.positionY).min
    val maxRow = oneLines.size() - 1

    //val offSet = locationMap.groupBy(x=>x.positionY)

    try {
      //TODO : "" as FileName
      IndexedSeq("" -> (for(index <- minRow to maxRow) yield {
        val oneLine = oneLines.get(index)
        locationMap.map {
          x =>
            val target = if (oneLine.size > x.positionX) {
              oneLine.get(x.positionX)
            } else {
              null
              //throw new Exception(s"Row ${index} Column ${x.positionX} not found")
            }
            x.name -> target
        }.toMap
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
    val parser = getFormatter

    val outputStreamWriter = new OutputStreamWriter(outputStream, encoding)
    val writer = parser.print(outputStreamWriter)

    try{
      bindData.map {
        x =>
          val map = x._2
          val m = locationMap.map {
            l =>
              if (map.contains(l.name)) {
                map(l.name) match {
                  case null => ""
                  case mm:Date =>
                    s"${LocalDateTime.ofInstant(mm.toInstant, ZoneId.systemDefault()).format(Config.DateReadParse)}"
                  case mm:String =>
                    s"${map(l.name).toString}"
                  case _ =>
                    map(l.name).toString
                }
              } else {
                null
              }
          }
          writer.printRecord(m:_*)
      }

    } catch {
      case ex:Throwable =>
        throw ex
    } finally {
      writer.flush()
      writer.close()
      outputStreamWriter.close()
      outputStream.close()
    }

  }

  override def _setDataDown(bindData: (String, IndexedSeq[Map[String, Any]])*): Unit = {
    val parser = getFormatter

    val outputStreamWriter = new OutputStreamWriter(outputStream, encoding)
    val writer = parser.print(outputStreamWriter)

    try{
      bindData.map {
        b =>
          val x = b._2
          x.map {
            xx =>
              val map = xx
              val m = locationMap.map {
                l =>
                  if(map.contains(l.name)) {
                    map(l.name) match {
                      case null => ""
                      case mm:Date =>
                        s"${LocalDateTime.ofInstant(mm.toInstant, ZoneId.systemDefault()).format(Config.DateReadParse)}"
                      case mm:String =>
                        s"${map(l.name).toString}"
                      case _ =>
                        map(l.name).toString
                    }
                  } else {
                    null
                  }
              }
              writer.printRecord(m:_*)
          }
      }

    } catch {
      case ex:Throwable =>
        throw ex
    } finally {
      writer.flush()
      writer.close()
      outputStreamWriter.close()
      outputStream.close()
    }

  }

}
