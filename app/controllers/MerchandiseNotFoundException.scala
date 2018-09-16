package controllers

case class MerchandiseNotFoundException(exceptionMessage: String) extends RuntimeException(exceptionMessage)
