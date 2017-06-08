package org.valet.common

import org.valet.common.Loaders.ConfDto

case class ScaffoldDtos(
                         confDto: ConfDto,
                         generatedTables: Seq[GeneratedTable]
                       )


case class ConfTable(
                      tableName: String,
                      relation: String,
                      isScaffoldList: Seq[String],
                      isRollList: Seq[String],
                      columnList: Seq[ConfTableColumn]
                    )

case class ConfTableColumn(
                            cName: String,
                            cType: String,
                            cAttr: Seq[String]
                          )

case class GeneratedTable(
                           tableName: String,
                           columnList: Seq[GeneratedColumn]
                         )

case class GeneratedColumn(
                            columnName: String,
                            columnType: String
                          )
