package model

import java.time.LocalDate
import java.util.{Currency, UUID}

case class Offer(merchandise: Merchandise, expiryDate: LocalDate, description: String, price: Price, active: Boolean)

case class Merchandise(id: UUID, merchant: Merchant)
class Product(id: UUID, merchant: Merchant) extends Merchandise(id, merchant)
class Service(id: UUID, merchant: Merchant) extends Merchandise(id, merchant)

case class Price(currency: Currency, amount: BigDecimal)

object Price {
  def apply(currency: String, amount: BigDecimal): Price = new Price(Currency.getInstance(currency), amount)
}

case class Merchant(id: UUID)


