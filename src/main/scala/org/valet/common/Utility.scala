package org.valet.common

import java.io.{File, IOException}
import java.net.URL
import java.nio.charset.Charset

import org.apache.commons.io.FileUtils

import scala.sys.process._

trait Utility {

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

  def cli(command: String): Int = {
    command.!
  }

  def echoErrorMsg(e: String): Unit = {
    System.out.println(
      s"""
         |-- ERROR OCCURRED --
         |$e
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

  def forceMkdir(file: File): Unit = {
    FileUtils.forceMkdir(file)
  }

  def forceWrite(fullpath: String, body: String): Unit = {
    tryOpt {
      forceWrite(new File(fullpath), body)
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
    val list: Seq[java.io.File] = new java.io.File(dir).listFiles.toSeq
    list.flatMap { x =>
      if (x.isDirectory) {
        getDirFileList(x.getPath, list)
      } else {
        result.:+(x)
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
      case _: IOException => false
    }
  }

}
