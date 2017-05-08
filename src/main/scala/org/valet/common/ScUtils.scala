package org.valet.common

import java.io.{File, IOException}
import java.net.URL
import java.nio.charset.Charset

import org.apache.commons.io.FileUtils
import skinny.util.StringUtil

import scala.sys.process._

object ScUtils extends ScProjectUtils {
  val ANSI_RESET = "\u001B[0m"
  val ANSI_BLACK = "\u001B[30m"
  val ANSI_RED = "\u001B[31m"
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_YELLOW = "\u001B[33m"
  val ANSI_BLUE = "\u001B[34m"
  val ANSI_PURPLE = "\u001B[35m"
  val ANSI_CYAN = "\u001B[36m"
  val ANSI_WHITE = "\u001B[37m"

  val ANSI_BLACK_BACKGROUND = "\u001B[40m"
  val ANSI_RED_BACKGROUND = "\u001B[41m"
  val ANSI_GREEN_BACKGROUND = "\u001B[42m"
  val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
  val ANSI_BLUE_BACKGROUND = "\u001B[44m"
  val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
  val ANSI_CYAN_BACKGROUND = "\u001B[46m"
  val ANSI_WHITE_BACKGROUND = "\u001B[47m"

  def charset: Charset = Charset.defaultCharset()

  def toCamelCase(v: String): String = StringUtil.toCamelCase(v)

  def toSnakeCase(v: String): String = StringUtil.toSnakeCase(v)

  def toSplitName(v: String): String = toSnakeCase(v).split("_").toSeq.mkString(" ")

  def toFirstCharUpper(s: String): String = s.head.toUpper + s.tail

  def toFirstCharLower(s: String): String = s.head.toLower + s.tail

  def tryOpt[T](f: => T)
               (implicit onError: Throwable => Option[T] = { t: Throwable =>
                 t match {
                   case c1: com.typesafe.config.ConfigException => echoErrorMsg(c1.getMessage); None
                   case c2: Exception                           => echoErrorMsg(c2.getMessage); None
                 }
               }): Option[T] = {
    try {
      Some(f)
    } catch {
      case c: Throwable => onError(c)
    }
  }

  def toSingleSpace(str: String): String = {
    val ch: Array[Char] = str.toCharArray
    ch.foldLeft("")((a, b) => {
      if (a.isEmpty) {
        b.toString
      } else {
        if (a.toCharArray.last == ' ' && b == ' ') {
          a
        } else {
          a + b
        }
      }
    }
    )
  }

  def wrapSelfGoodly[T](v: T): Option[T] = {
    v match {
      case _: Int    =>
        Some(v)
      case k: String =>
        if (k.isEmpty) None else Some(v)
      case k: Seq[_] =>
        if (k.isEmpty) None else Some(v)
      case _         =>
        Some(v)
    }
  }

  def cli(command: String) = {
    command.!
  }

  def echoErrorMsg(e: String) = {
    System.out.println(
      s"""
         |-- ERROR OCCURRED --
         |${e}
          """.stripMargin
    )
  }

  def makeFileIfNotExist(file: File): Unit = {
    file.createNewFile()
  }

  def writeIfAbsent(file: File, code: String) {
    FileUtils.forceMkdir(file.getParentFile)
    if (file.exists()) {
      println("  \"" + file.getPath + "\" skipped.")
    } else {
      FileUtils.write(file, code, charset)
      println("  \"" + file.getPath + "\" created.")
    }
  }

  def forceWrite(file: File, code: String) {
    FileUtils.forceMkdir(file.getParentFile)
    if (file.exists()) {
      FileUtils.write(file, code, charset)
      println("  \"" + file.getPath + "\" modified.")
    } else {
      FileUtils.write(file, code, charset)
      println("  \"" + file.getPath + "\" created.")
    }
  }

  def writeAppending(file: File, code: String) {
    FileUtils.forceMkdir(file.getParentFile)
    if (file.exists()) {
      FileUtils.write(file, code, charset, true)
      println("  \"" + file.getPath + "\" modified.")
    } else {
      FileUtils.write(file, code, charset)
      println("  \"" + file.getPath + "\" created.")
    }
  }

  def getDirFileList(dir: String, result: Seq[java.io.File]): Seq[java.io.File] = {
    val list: Seq[java.io.File] = (new java.io.File(dir).listFiles).toSeq
    list.flatMap { x =>
      x.isDirectory match {
        case true  => getDirFileList(x.getPath, list)
        case false => result.:+(x)
      }
    }.distinct.sortBy(f => f.toString)
  }

  def isActiveInternet: Boolean = {
    try {
      val url = new URL("http://google.com")
      val con = url.openConnection
      con.getInputStream
      true
    } catch {
      case e: IOException => false
    }
  }

}

trait ScProjectUtils {

  def pTableName(table: SlickTable): String = {
    ScUtils.toFirstCharLower(table.tableName.dropRight(3))
  }

}