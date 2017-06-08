package org.valet.common

trait ValetUtility extends Utility with ValetConst {

  def isScaffoldOk(key: String, dtos: ScaffoldDtos, nowTable: GeneratedTable): Boolean = {
    getIsScaffoldList(dtos, nowTable).contains(key)
  }

  def getIsScaffoldList(dtos: ScaffoldDtos, nowTable: GeneratedTable): Seq[String] = {
    val isScaffoldList: Seq[String] = dtos.confDto.tableTableList
      .filter(f => f.tableName == getTableName(nowTable))
      .flatMap(_.isScaffoldList)
    isScaffoldList
  }

  def getOutputOptionList(dtos: ScaffoldDtos): Seq[(String, String)] = {
    dtos.confDto.generalOutputOptions.map(x => (x.split(':').head, x.split(':').last))
  }

  def getTableName(generatedTable: GeneratedTable): String = {
    generatedTable.tableName.dropRight(3)
  }

  def getAgTableName(generatedTable: GeneratedTable): String = {
    agPrefix + getTableName(generatedTable)
  }

  def getTableFieldName(generatedTable: GeneratedTable): String = {
    toFirstCharLower(generatedTable.tableName.dropRight(3))
  }

  def getAgTableFieldName(generatedTable: GeneratedTable): String = {
    agPrefix + getTableFieldName(generatedTable)
  }

  def getTableFieldName(className: String): String = {
    toFirstCharLower(className)
  }

  def getLike(className: String): String = {
    className + suffixLike
  }

  def getObj(className: String): String = {
    className + suffixObject
  }

  // custom
  def getAgCreateForm(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + createFormStr + suffixForm
  }

  def getAgEditForm(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + editFormStr + suffixForm
  }

  def getAgMappingForm(generatedTable: GeneratedTable): String = {
    agPrefix + mappingFormPrefix + getTableName(generatedTable) + suffixForm
  }


  // Dao
  def getDao(generatedTable: GeneratedTable): String = {
    getTableName(generatedTable) + suffixDao
  }

  def getAgDao(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + suffixDao
  }

  def getDaoLike(generatedTable: GeneratedTable): String = {
    getLike(getTableName(generatedTable) + suffixDao)
  }

  def getAgDaoLike(generatedTable: GeneratedTable): String = {
    getLike(getAgTableName(generatedTable) + suffixDao)
  }

  // Service
  def getService(generatedTable: GeneratedTable): String = {
    getTableName(generatedTable) + suffixService
  }

  def getAgService(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + suffixService
  }

  def getServiceLike(generatedTable: GeneratedTable): String = {
    getLike(getTableName(generatedTable) + suffixService)
  }

  def getAgServiceLike(generatedTable: GeneratedTable): String = {
    getLike(getAgTableName(generatedTable) + suffixService)
  }

  // Form
  def getForm(generatedTable: GeneratedTable): String = {
    getTableName(generatedTable) + suffixForm
  }

  def getAgForm(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + suffixForm
  }

  def getFormLike(generatedTable: GeneratedTable): String = {
    getLike(getTableName(generatedTable) + suffixForm)
  }

  def getAgFormLike(generatedTable: GeneratedTable): String = {
    getLike(getAgTableName(generatedTable) + suffixForm)
  }

  // Controller
  def getController(generatedTable: GeneratedTable): String = {
    getTableName(generatedTable) + suffixController
  }

  def getAgController(generatedTable: GeneratedTable): String = {
    getAgTableName(generatedTable) + suffixController
  }

  def getControllerLike(generatedTable: GeneratedTable): String = {
    getLike(getTableName(generatedTable) + suffixController)
  }

  def getAgControllerLike(generatedTable: GeneratedTable): String = {
    getLike(getAgTableName(generatedTable) + suffixController)
  }

}
