package foo

import java.io._

import com.typesafe.config.{Config, ConfigFactory}

import scalaj.http.{Http, HttpRequest, HttpResponse}


object Launcher {

  def main(args: Array[String]) {
    val baseLangage = "ja"

    Seq("en", "fr", "zh-cn", "zh-tw").foreach { ln =>
      generateI18nMsgFile(ln, baseLangage)
    }
  }

  private def generateI18nMsgFile(toLang: String, baseLangage: String) = {
    val conf: Config = ConfigFactory.parseFile(new File("./azure-key.conf"))
    val apikey = conf.getString("key")
    val token = getToken(apikey).get

    val inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(s"./conf/messages.${baseLangage}"), "UTF-8"))
    val outBuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(s"./conf/messages.${toLang}"), "UTF-8"))

    val lines: Seq[String] = (Iterator.continually(inBuffer.readLine()).takeWhile(_ != null).toList)
    val resultList: Seq[String] = lines.map { line =>
      val confKey = line.split("=").headOption.getOrElse("")
      val confVal = line.split("=").last

      val resultConfVal = translate(token, confVal, toLang).getOrElse("")
      val resultConfVal2: String = resultConfVal.replaceAll("""<("[^"]*"|'[^']*'|[^'">])*>""", "")
      confKey + "=" + resultConfVal2
    }
    val buf2 = new StringBuilder
    resultList.foreach { f =>
      buf2.append(f)
      buf2.append("\n")
    }
    outBuffer.write(buf2.toString)
    outBuffer.flush()
  }

  private def getToken(apikey: String): Option[String] = {
    try {
      val request: HttpRequest = Http("https://api.cognitive.microsoft.com/sts/v1.0/issueToken").headers(Seq(
        ("Content-Type", "application/json"),
        ("Accept", "application/jwt"),
        ("Ocp-Apim-Subscription-Key", apikey)
      )).postForm

      val res: HttpResponse[String] = request.asString
      if (res.code == 200) {
        Some(res.body)
      } else {
        None
      }
    } catch {
      case e: Throwable => System.out.println("" + e); None
    }
  }

  private def translate(token: String, src: String, toLang: String): Option[String] = {
    try {
      val key = "Bearer " + token
      val request: HttpRequest = Http("https://api.microsofttranslator.com/v2/http.svc/Translate").params(Seq(
        ("appid", key),
        ("Accept", "application/xml"),
        ("text", src),
        ("to", toLang)
      ))

      val res: HttpResponse[String] = request.asString
      if (res.code == 200) {
        Some(res.body)
      } else {
        None
      }
    } catch {
      case e: Throwable => System.out.println("" + e); None
    }
  }
}
