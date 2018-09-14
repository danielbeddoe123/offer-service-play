package data

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import slick.jdbc.H2Profile.api._
import slick.lifted.{TableQuery, Tag}

object tables {

  class Merchants(tag: Tag) extends Table[Merchant](tag, "MERCHANT") {
    def id = column[UUID]("MERCHANT_ID", O.PrimaryKey)
    def name = column[String]("MERCHANT_NAME")
    def * = (id, name) <> (Merchant.tupled, Merchant.unapply)
  }
  val merchants = TableQuery[Merchants]
  case class Merchant(id: UUID, name: String)

  class Merchandises(tag: Tag) extends Table[Merchandise](tag, "MERCHANDISE") {
    def id = column[UUID]("MERCHANDISE_ID", O.PrimaryKey)
    def mType = column[String]("MERCHANDISE_TYPE")
    def merchantId = column[UUID]("MERCHANT_ID")
    def * = (id, mType, merchantId) <> (Merchandise.tupled, Merchandise.unapply)

    def merchantIdFK = foreignKey("MERCHANT_ID_FK", merchantId, merchants)(_.id)
  }
  val merchandises = TableQuery[Merchandises]
  case class Merchandise(id: UUID, mType: String, merchantId: UUID)

  private implicit val localDateToDate = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )
  class Offers(tag: Tag) extends Table[Offer](tag, "OFFER") {
    def id = column[UUID]("OFFER_ID", O.PrimaryKey)
    def description = column[String]("DESCRIPTION")
    def merchandiseId = column[UUID]("MERCHANDISE_ID")
    def currency = column[String]("CURRENCY")
    def price = column[BigDecimal]("PRICE")
    def active = column[Boolean]("ACTIVE")
    def expiryDate = column[LocalDate]("EXPIRY_DATE")

    def * = (id, description, merchandiseId, currency, price, active, expiryDate) <> (Offer.tupled, Offer.unapply)

    def merchandiseIdFK = foreignKey("MERCHANDISE_ID_FK", merchandiseId, merchandises)(_.id)
  }
  val offers = TableQuery[Offers]
  case class Offer(id: UUID, description: String, merchandiseId: UUID, currency: String, price: BigDecimal, active: Boolean, expiryDate: LocalDate)
}
