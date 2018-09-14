package utils

import java.time.format.DateTimeFormatter

import model.Offer

object DataRequestFactory {

  def generateJson(offer: Offer): String = {
    val expiryDate = offer.expiryDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    s""" {
       |     "expiryDate": "$expiryDate",
       |     "description": "${offer.description}",
       |     "price":   {
       |        "currency": "${offer.price.currency.getCurrencyCode}",
       |        "amount": ${offer.price.amount}
       |     },
       |     "active": ${offer.active}
       |  }
    """.stripMargin
  }
}
