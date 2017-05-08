package org.valet.common

/**
  * Created by keigo on 2017/04/05.
  */
trait ScaffoldConsts {
  val UTF8 = "UTF-8"

  // conf params
  val CONF_ENGINE_DAO_SLICK = "slick"
  val CONF_ENGINE_VIEW_TWIRL = "twirl"

  val agPackageName = "autogen"
  val agPath = s"/${agPackageName}"
  val agPack = s".${agPackageName}"
  val dirCommonUtils = "utils"
  val dirCommonAuth = "auth"
  val pathCommonAuth = "/" + dirCommonAuth
  val packageCommonAuth = "." + dirCommonAuth
  val pathCommonUtils = "/" + dirCommonUtils
  val packageCommonUtils = "." + dirCommonUtils

  val prefixCommon = "Ag"
  val prefixDto = ""
  val prefixDao = "Ag"
  val prefixService = "Ag"
  val prefixForm = "Ag"
  val prefixView = ""
  val prefixController = ""
  val prefixRepository = ""
  val prefixBSLogic = ""

  val commonRollName = "RollConfig"
  val commonAuthConfigName = "AuthConfig"
  val commonConstNameAG = prefixCommon + "Const"
  val commonConstNameCU = "MyConst"
  val commonUtilsNameAG = prefixCommon + "Utils"
  val commonUtilsNameCU = "MyUtils"

  // service
  val serviceUtilsName = prefixService + "Utils"

  // form
  val formDtoNameAG = prefixForm + "FormDto"
  val formDtoNameCU = "FormDto"
  val formValidationNameAG = prefixForm + "FormValidation"
  val formValidationNameCU = "FormValidation"
  val formMappingNameAG = prefixForm + "FormMapping"
  val formMappingNameCU = "FormMapping"
  val formConstraintsCU = "FormValidConstraints"

  // dto
  val dtoViewDtoName = prefixDto + "PlayDto"
  val dtoExceptionDtoName = prefixDto + "MyExceptionDto"

  // controller
  val controllNameApplication = "Home"
  val controllNameAdmin = "Home"

  val METHOD_SHOWINDEX = "showIndex"
  val METHOD_SHOWDETAIL = "showDetail"
  val METHOD_SHOWCREATE = "showCreate"
  val METHOD_SHOWEDIT = "showEdit"
  val METHOD_STORE = "store"
  val METHOD_UPDATE = "update"
  val METHOD_DESTROY = "destroy"

}
