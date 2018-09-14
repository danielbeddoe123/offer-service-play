package controllers
import model.Price

package json {
  object ReadFormats {
    import play.api.libs.json._
    import play.api.libs.functional.syntax._

    implicit val price: Reads[Price] = (
      (__ \ "currency").read[String] and
        (__ \ "amount").read[BigDecimal]
      )(Price.apply(_, _))
    implicit val offerResourceReads = Json.reads[OfferResource]
  }

}

case class OfferResource(expiryDate: String, description: String, price: Price, active: Boolean)
