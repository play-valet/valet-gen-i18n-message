package org.valet.common

import org.valet.common.ScaffoldLoader.ConfDto

case class ScaffoldDtos(confDto: ConfDto,
                         paramDto: PathDto,
                         tables: Seq[SlickTable])


case class ScaffoldTable(
                          tableName: String,
                          relation: String,
                          isScaffoldList: Seq[String],
                          isRollList: Seq[String],
                          columnList: Seq[ScaffoldTableColumn]
                        )

case class ScaffoldTableColumn(
                                cName: String,
                                cType: String,
                                cAttr: Seq[String]
                              )

case class SlickTable(
                       tableName: String,
                       columnList: Seq[SlickColumn]
                     )

case class SlickColumn(
                        columnName: String,
                        columnType: String
                      )

case class PathDto(
                    pathCommon: String,
                    packageCommon: String,
                    pathCommonAuth: String,
                    packageCommonAuth: String,
                    pathCommonAuthAG: String,
                    packageCommonAuthAG: String,
                    classNameCommonAuthRoll: String,
                    fullpathCommonAuthRoll: String,
                    classNameCommonAuthAuthConfigLike: String,
                    fullpathCommonAuthAuthConfigLike: String,
                    pathCommonUtils: String,
                    packageCommonUtils: String,
                    pathCommonUtilsAG: String,
                    packageCommonUtilsAG: String,
                    classNameCommonUtilsAgUtils: String,
                    fullpathCommonUtilsAgUtilsAG: String,
                    fullpathCommonUtilsAgUtilsCU: String,
                    classNameCommonUtilsAgConst: String,
                    fullpathCommonUtilsAgConstAG: String,
                    fullpathCommonUtilsAgConstCU: String,
                    pathController: String,
                    packageController: String,
                    pathDto: String,
                    packageDto: String,
                    classNameDtoViewDto: String,
                    pathDtoViewDto: String,
                    packageDtoViewDto: String,
                    fullpathDtoViewDto: String,
                    classNameDtoPlayDto: String,
                    pathDtoPlayDto: String,
                    packageDtoPlayDto: String,
                    fullpathDtoPlayDto: String,
                    pathForm: String,
                    pathFormAG: String,
                    packageForm: String,
                    packageFormAG: String,
                    pathModelEntity: String,
                    packageModelEntity: String,
                    pathModelBLogic: String,
                    packageModelBLogic: String,
                    pathModelService: String,
                    packageModelService: String,
                    pathModelRepository: String,
                    packageModelRepository: String,
                    pathModelDao: String,
                    packageModelDao: String,
                    pathView: String,
                    packageView: String,
                    pathConf: String
                  )

case class SimpleEnumSrc(enumName: String, fieldList: Seq[EnumField])

case class EnumField(id: Int, name: String)