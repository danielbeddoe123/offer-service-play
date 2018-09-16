package data

import java.time.LocalDate.now
import java.util.UUID

import _root_.data.tables.{Merchandise, Merchant, Offer}
import db.InMemoryDatabaseSetup
import play.api._
import play.api.inject.bind

class OfferRepositorySpec extends InMemoryDatabaseSetup {

  override def fakeApplication(): Application = {
    RepositoryApplicationBuilder.fakeApplicationWithBinding(
      bind[OfferRepository].to[OfferRepositoryImpl])
  }

  "offerRepository" should {
    setupDB
    val repository: OfferRepository = app.injector.instanceOf[OfferRepository]

    "create an offer" in {
      val merchantId = UUID.randomUUID
      val merchandiseId = UUID.randomUUID
      insertMerchant(Merchant(merchantId, "Merchant 1"))
      insertMerchandise(Merchandise(merchandiseId, "PRODUCT", merchantId))

      val returnCode = repository.create(Offer(UUID.randomUUID(), "Some offer", merchandiseId, "GBP", BigDecimal(10.00), active = true, now().plusMonths(1)))

      assert(returnCode.futureValue == 1)
      val offers: Seq[Offer] = fetchOffers()
      assert(offers.size == 1)
    }
  }

}
