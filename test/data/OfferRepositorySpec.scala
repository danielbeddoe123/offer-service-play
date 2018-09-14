package data

import java.time.LocalDate.now
import java.util.UUID

import controllers.InMemoryDatabaseSetup
import org.scalatest.mockito.MockitoSugar
import play.api._
import _root_.data.tables.{Merchant, Offer, Merchandise}
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import service.OfferService

import scala.concurrent.ExecutionContext.Implicits.global

class OfferRepositorySpec extends InMemoryDatabaseSetup with MockitoSugar {

  override def fakeApplication(): Application = {
    val application = new GuiceApplicationBuilder()
      .bindings(bind[OfferService].toInstance(mock[OfferService]))
      .in(Mode.Test)
      .build()
    application
  }
  "offerRepository" should {
    setupDB
    val repository: OfferRepository = app.injector.instanceOf[OfferRepositoryImpl]

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

    "find all offers" in {
      val merchantId = UUID.randomUUID
      val merchandiseId = UUID.randomUUID
      insertMerchant(Merchant(merchantId, "Merchant 1"))
      insertMerchandise(Merchandise(merchandiseId, "PRODUCT", merchantId))
      val offer = Offer(UUID.randomUUID(), "Some offer", merchandiseId, "GBP", BigDecimal(10.00), active = false, now().plusMonths(1))
      insertOffer(offer)

      val allOffers = repository.findAll()

      allOffers.map(offers => {
        assert(offers.size == 1)
        assert(offers.head == offer)
      })
    }
  }

}
