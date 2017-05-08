package org.valet.common

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}

import com.typesafe.config.{Config, ConfigFactory}
import org.valet.common.ScUtils.tryOpt

import scala.collection.JavaConversions._

import scala.meta._

/**
  * Created by keigo on 2017/04/11.
  */
object ScaffoldLoader extends ScaffoldConsts {

  def getKeyListFromHocon(filePath: String, hoconPath: Option[String]): Seq[String] = {
    val conf: Config = ConfigFactory.parseFile(new File(filePath))
    getKeyListFromHocon(conf, hoconPath)
  }

  def getKeyListFromHocon(conf: Config, hoconPath: Option[String]): Seq[String] = {
    hoconPath match {
      case Some(c) => getKeyListFromHocon(conf.getConfig(c))
      case None    => getKeyListFromHocon(conf)
    }
  }

  def getKeyListFromHocon(conf: Config, hoconPath: String): Seq[String] = {
    getKeyListFromHocon(conf.getConfig(hoconPath))
  }

  def getKeyListFromHocon(conf: Config): Seq[String] = {
    var lst = List[String]()
    for {
      tbl <- conf.entrySet()
    } yield {
      lst = lst :+ tbl.getKey
    }
    lst.map(x => x.toString).sortBy(f => f)
  }


  def getScaffoldDtos(filepath: String): ScaffoldDtos = {
    val conf: Config = ConfigFactory.parseFile(new File(filepath))
    val confDto: ConfDto = ScaffoldLoader.getConfDto(conf)
    val paramDto: PathDto = ScaffoldLoader.getClassParamsDto(confDto)
    val tables: Seq[SlickTable] = getSlickTableList(confDto).filter(_.tableName != "SchemaVersionRow")
    ScaffoldDtos(confDto, paramDto, tables)
  }

  def getSlickTableListFromCaseClass(caseClass: String): Option[SlickTable] = {
    val clsx: Source = caseClass.parse[Source].get
    clsx.collect {
      case cls: Defn.Class =>
        SlickTable(
          cls.name.syntax.toString,
          cls.ctor.paramss.flatten.map { param =>
            val className = cls.name.syntax
            val fieldName = Term.Name(param.name.syntax)
            val fieldType = param.decltpe.getOrElse(abort(s"${param.name.syntax} does not have type"))
            val argName = Term.fresh("text")
            val returnType = Type.Name(fieldType.syntax)
            //            System.out.println(s"""${className}\t${fieldName}\t$fieldType""");
            //            System.out.println(s"""def $fieldName($argName: $fieldType):(_root_.scala.Predef.String, $returnType) = (${fieldName.text}, $argName)""");
            SlickColumn(fieldName.toString(), fieldType.toString())
          }
        )
    }.headOption
  }

  def getSlickTableList(setting: ConfDto): Seq[SlickTable] = {
    val modelpath = setting.outputPathModel + "/Tables.scala"
    val inBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(modelpath), UTF8))
    val lines = Iterator.continually(inBuffer.readLine()).takeWhile(_ != null).toList
    (for ((line, lineNum) <- lines.zipWithIndex) yield (line.replaceAll("^\\s*", "")))
      .filter(_.startsWith("case class"))
      .map(x => getSlickTableListFromCaseClass(x))
      .filter(_.isDefined)
      .map(_.get)
  }


  def getScaffoldTable(conf: Config): Option[Seq[ScaffoldTable]] = tryOpt(
    for {
      tbl <- conf.getConfigList("scaffold.table.tableList")
    } yield {
      val keyListInTable = getKeyListFromHocon(tbl, None)
      ScaffoldTable(
        tbl.getString("tableName"),
        tbl.getString("relation"),
        tbl.getStringList("isScaffoldList"),
        tbl.getStringList("isRollList"),
        getScaffoldTableColumn(tbl).get
      )
    }
  )

  def getScaffoldTableColumn(conf: Config): Option[Seq[ScaffoldTableColumn]] = {
    tryOpt(
      for {
        tbl <- conf.getConfigList("columnList")
      } yield {
        ScaffoldTableColumn(
          tbl.getString("cName"),
          tbl.getString("cType"),
          tbl.getStringList("cAttr")
        )
      }
    )
  }

  def getClassParamsDto(setting: ConfDto): PathDto = {
    PathDto(
      pathCommon = setting.outputPathCommon
      , packageCommon = setting.outputPackageCommon
      , pathCommonAuth = setting.outputPathCommon + "/auth"
      , packageCommonAuth = setting.outputPackageCommon + ".auth"
      , pathCommonAuthAG = setting.outputPathCommon + "/auth" + "/" + agPackageName
      , packageCommonAuthAG = setting.outputPackageCommon + ".auth" + "." + agPackageName
      , classNameCommonAuthRoll = commonRollName
      , fullpathCommonAuthRoll = setting.outputPathCommon + "/auth" + s"/${commonRollName}.scala"
      , classNameCommonAuthAuthConfigLike = commonAuthConfigName
      , fullpathCommonAuthAuthConfigLike = setting.outputPathCommon + "/auth" + s"/${commonAuthConfigName}Like.scala"
      , pathCommonUtils = setting.outputPathCommon + "/utils"
      , packageCommonUtils = setting.outputPackageCommon + ".utils"
      , pathCommonUtilsAG = setting.outputPathCommon + "/utils" + "/" + agPackageName
      , packageCommonUtilsAG = setting.outputPackageCommon + ".utils" + "." + agPackageName
      , classNameCommonUtilsAgUtils = commonUtilsNameCU
      , fullpathCommonUtilsAgUtilsAG = setting.outputPathCommon + "/utils" + "/" + agPackageName + s"/${commonUtilsNameAG}.scala"
      , fullpathCommonUtilsAgUtilsCU = setting.outputPathCommon + "/utils" + "/" + s"/${commonUtilsNameCU}.scala"
      , classNameCommonUtilsAgConst = commonConstNameCU
      , fullpathCommonUtilsAgConstAG = setting.outputPathCommon + "/utils" + "/" + agPackageName + s"/${commonConstNameAG}.scala"
      , fullpathCommonUtilsAgConstCU = setting.outputPathCommon + "/utils" + "/" + s"/${commonConstNameCU}.scala"
      , pathController = setting.outputPathController
      , packageController = setting.outputPackageController
      , pathDto = setting.outputPathDto
      , packageDto = setting.outputPackageDto
      , classNameDtoViewDto = dtoViewDtoName
      , pathDtoViewDto = setting.outputPathDto
      , packageDtoViewDto = setting.outputPackageDto
      , fullpathDtoViewDto = setting.outputPathDto + s"/${dtoViewDtoName}.scala"
      , classNameDtoPlayDto = "PlayDto"
      , pathDtoPlayDto = setting.outputPathDto
      , packageDtoPlayDto = setting.outputPackageDto
      , fullpathDtoPlayDto = setting.outputPathDto + "/PlayDto.scala"
      , pathForm = setting.outputPathForm
      , pathFormAG = setting.outputPathForm + "/" + agPackageName
      , packageForm = setting.outputPackageForm
      , packageFormAG = setting.outputPackageForm + "." + agPackageName
      , pathModelEntity = setting.outputPathModel
      , packageModelEntity = setting.outputPackageModel
      , pathModelBLogic = setting.outputPathService
      , packageModelBLogic = setting.outputPackageService
      , pathModelService = setting.outputPathService + "/" + agPackageName
      , packageModelService = setting.outputPackageService + "." + agPackageName
      , pathModelRepository = setting.outputPathRepository
      , packageModelRepository = setting.outputPackageRepository
      , pathModelDao = setting.outputPathRepository + "/" + agPackageName
      , packageModelDao = setting.outputPackageRepository + "." + agPackageName
      , pathView = setting.outputPathView
      , packageView = setting.outputPackageView
      , pathConf = "conf"
    )
  }

  def getConfDto(conf: Config) = {
    ConfDto(
      dbDb = conf.getString("scaffold.db.db"),
      dbDefaultDriver = conf.getString("scaffold.db.default.driver"),
      dbDefaultPassword = conf.getString("scaffold.db.default.password"),
      dbDefaultUrl = conf.getString("scaffold.db.default.url"),
      dbDefaultUsername = conf.getString("scaffold.db.default.username"),
      dbIsUseDocker = conf.getString("scaffold.db.isUseDocker"),
      enginesDao = conf.getString("scaffold.engines.dao"),
      enginesJs = conf.getString("scaffold.engines.js"),
      enginesMigration = conf.getString("scaffold.engines.migration"),
      enginesView = conf.getString("scaffold.engines.view"),
      modulesAuditLogIsUse = conf.getString("scaffold.modules.auditLog.isUse"),
      modulesAuditLogTableTableName = conf.getString("scaffold.modules.auditLog.table.tableName"),
      modulesAuthIsUse = conf.getString("scaffold.modules.auth.isUse"),
      modulesAuthLibrary = conf.getString("scaffold.modules.auth.library"),
      modulesAuthRoleList = conf.getStringList("scaffold.modules.auth.roleList"),
      modulesAuthTableLoginIdColumnName = conf.getString("scaffold.modules.auth.table.LoginIdColumnName"),
      modulesAuthTableLoginPassColumnName = conf.getString("scaffold.modules.auth.table.LoginPassColumnName"),
      modulesAuthTableAccountTableName = conf.getString("scaffold.modules.auth.table.accountTableName"),
      modulesErrorLogIsUse = conf.getString("scaffold.modules.errorLog.isUse"),
      modulesErrorLogTableTableName = conf.getString("scaffold.modules.errorLog.table.tableName"),
      modulesI18nCrudTableI18nList = conf.getStringList("scaffold.modules.i18nCrudTable.i18nList"),
      modulesI18nCrudTableIsUse = conf.getString("scaffold.modules.i18nCrudTable.isUse"),
      modulesI18nCrudTableTableTableName = conf.getString("scaffold.modules.i18nCrudTable.table.tableName"),
      modulesI18nMessageConfI18nList = conf.getStringList("scaffold.modules.i18nMessageConf.i18nList"),
      modulesI18nMessageConfIsUse = conf.getString("scaffold.modules.i18nMessageConf.isUse"),
      modulesTwirlScaffoldThemesEnableList = conf.getStringList("scaffold.modules.twirlScaffoldThemes.enableList"),
      modulesTwirlScaffoldThemesIsUse = conf.getString("scaffold.modules.twirlScaffoldThemes.isUse"),
      modulesTwirlScaffoldThemesSourceAccount = conf.getString("scaffold.modules.twirlScaffoldThemes.source.account"),
      modulesTwirlScaffoldThemesSourceBackendAdmin = conf.getString("scaffold.modules.twirlScaffoldThemes.source.backend.admin"),
      modulesTwirlScaffoldThemesSourceFrontBlog = conf.getString("scaffold.modules.twirlScaffoldThemes.source.front.blog"),
      modulesTwirlScaffoldThemesSourceFrontCorporate = conf.getString("scaffold.modules.twirlScaffoldThemes.source.front.corporate"),
      modulesTwirlScaffoldThemesSourceFrontStatusErrors = conf.getString("scaffold.modules.twirlScaffoldThemes.source.front.statusErrors"),
      outputDirectory = conf.getString("scaffold.output.directory"),
      outputOptions = conf.getStringList("scaffold.output.options"),
      outputPackageCommon = conf.getString("scaffold.output.path.common").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathCommon = conf.getString("scaffold.output.path.common"),
      outputPackageController = conf.getString("scaffold.output.path.controller").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathController = conf.getString("scaffold.output.path.controller"),
      outputPackageDto = conf.getString("scaffold.output.path.dto").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathDto = conf.getString("scaffold.output.path.dto"),
      outputPackageForm = conf.getString("scaffold.output.path.form").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathForm = conf.getString("scaffold.output.path.form"),
      outputPackageMigration = conf.getString("scaffold.output.path.migration").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathMigration = conf.getString("scaffold.output.path.migration"),
      outputPackageModel = conf.getString("scaffold.output.path.model").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathModel = conf.getString("scaffold.output.path.model"),
      outputPackageRepository = conf.getString("scaffold.output.path.repository").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathRepository = conf.getString("scaffold.output.path.repository"),
      outputPackageService = conf.getString("scaffold.output.path.service").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathService = conf.getString("scaffold.output.path.service"),
      outputPathSrcCurrent = conf.getString("scaffold.output.path.srcCurrent"),
      outputPackageView = conf.getString("scaffold.output.path.view").replace(conf.getString("scaffold.output.path.srcCurrent") + "/", "").replace("/", "."),
      outputPathView = conf.getString("scaffold.output.path.view"),
      tableTableList = getScaffoldTable(conf).get)
  }

  case class ConfDto(
                      dbDb: String,
                      dbDefaultDriver: String,
                      dbDefaultPassword: String,
                      dbDefaultUrl: String,
                      dbDefaultUsername: String,
                      dbIsUseDocker: String,
                      enginesDao: String,
                      enginesJs: String,
                      enginesMigration: String,
                      enginesView: String,
                      modulesAuditLogIsUse: String,
                      modulesAuditLogTableTableName: String,
                      modulesAuthIsUse: String,
                      modulesAuthLibrary: String,
                      modulesAuthRoleList: Seq[String],
                      modulesAuthTableLoginIdColumnName: String,
                      modulesAuthTableLoginPassColumnName: String,
                      modulesAuthTableAccountTableName: String,
                      modulesErrorLogIsUse: String,
                      modulesErrorLogTableTableName: String,
                      modulesI18nCrudTableI18nList: Seq[String],
                      modulesI18nCrudTableIsUse: String,
                      modulesI18nCrudTableTableTableName: String,
                      modulesI18nMessageConfI18nList: Seq[String],
                      modulesI18nMessageConfIsUse: String,
                      modulesTwirlScaffoldThemesEnableList: Seq[String],
                      modulesTwirlScaffoldThemesIsUse: String,
                      modulesTwirlScaffoldThemesSourceAccount: String,
                      modulesTwirlScaffoldThemesSourceBackendAdmin: String,
                      modulesTwirlScaffoldThemesSourceFrontBlog: String,
                      modulesTwirlScaffoldThemesSourceFrontCorporate: String,
                      modulesTwirlScaffoldThemesSourceFrontStatusErrors: String,
                      outputDirectory: String,
                      outputOptions: Seq[String],
                      outputPackageCommon: String,
                      outputPathCommon: String,
                      outputPackageController: String,
                      outputPathController: String,
                      outputPackageDto: String,
                      outputPathDto: String,
                      outputPackageForm: String,
                      outputPathForm: String,
                      outputPackageMigration: String,
                      outputPathMigration: String,
                      outputPackageModel: String,
                      outputPathModel: String,
                      outputPackageRepository: String,
                      outputPathRepository: String,
                      outputPackageService: String,
                      outputPathService: String,
                      outputPathSrcCurrent: String,
                      outputPackageView: String,
                      outputPathView: String,
                      tableTableList: Seq[ScaffoldTable])

}
