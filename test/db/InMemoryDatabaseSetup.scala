package db

import data.tables._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIOAction
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

abstract class InMemoryDatabaseSetup extends PlaySpec with GuiceOneServerPerSuite with ScalaFutures { this: Suite =>

  var dbConfigProvider: DatabaseConfigProvider = _
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  private val setup = DBIOAction.seq(
    (merchants.schema ++ merchandises.schema ++ offers.schema).create
  )

  def insertOffer(offer: Offer) = dbConfigProvider.get.db.run(offers += offer)
  def insertMerchant(merchant: Merchant) = dbConfigProvider.get.db.run(merchants += merchant)
  def insertMerchandise(merchandise: Merchandise) = dbConfigProvider.get.db.run(merchandises += merchandise)

  def setupDB: Unit = {
    dbConfigProvider = app.injector.instanceOf[DatabaseConfigProvider]
    dbConfigProvider.get.db.run(setup)
    validateSchema()
  }

  private def validateSchema(): Unit = {
    val tables = dbConfigProvider.get.db.run(MTable.getTables).futureValue
    assert(tables.size == 3)
  }

  def validateNumOffers(expected: Int) = {
    val offersData = dbConfigProvider.get.db.run(offers.result).futureValue
    assert(offersData.size == expected)
  }

  def fetchOffers() = {
    dbConfigProvider.get.db.run(offers.result).futureValue
  }
}
