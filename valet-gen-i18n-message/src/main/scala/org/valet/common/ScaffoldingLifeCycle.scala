package org.valet.common

/**
  * Created by keigo on 2017/05/07.
  */
trait ScaffoldingLifeCycle {

  def initAction(dtos: ScaffoldDtos)
  def mainAction(dtos: ScaffoldDtos)
  def endAction(dtos: ScaffoldDtos)

}
