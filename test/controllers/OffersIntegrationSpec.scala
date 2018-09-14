package controllers

import java.util.UUID

import data.tables.{Merchandise, Merchant}
import data.{OfferRepository, OfferRepositoryImpl}
import javax.inject.Inject
import model.Offer
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.inject.bind
import play.api.{Application, Mode}
import service.{OfferService, OfferServiceImpl}

class OffersIntegrationSpec extends InMemoryDatabaseSetup with MockitoSugar {

  private val offerId = UUID.randomUUID
  private val mockedOfferService = mock[OfferService]

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .bindings(bind[OfferService].toInstance(mockedOfferService))
      .bindings(bind[OfferRepository].to[OfferRepositoryImpl])
      .in(Mode.Test)
      .build()
  }

  private val merchandiseId = UUID.randomUUID
  private val merchantId = UUID.randomUUID

  private val wsClient = app.injector.instanceOf[WSClient]
  "submitting a POST request to the offer endpoint" should {
    setupDB

    "create an offer" in {
      mockedOfferService

      insertMerchant(Merchant(merchantId, "Merchant 1"))
      insertMerchandise(Merchandise(merchandiseId, "PRODUCT", merchantId))

      validateNumOffers(0)

      val serverUrl = s"localhost:$port"
      val createOfferUrl = s"http://$serverUrl/merchandise/$merchandiseId/offer"
      val request = wsClient.url(createOfferUrl)
        .withHttpHeaders("Content-Type" -> "application/json")

      val response = await(request.post(offerJson))

      response.status mustBe CREATED
      response.header("Location").value mustBe s"$createOfferUrl/$offerId"
      validateNumOffers(1)
    }
  }

  val offerJson: String = {
    val expiryDate = "2018-12-31"
    val priceAmount = 20.00
    s""" {
       |     "expiryDate": "$expiryDate",
       |     "description": "Some description",
       |     "price":   {
       |        "currency": "GBP",
       |        "amount": $priceAmount
       |     },
       |     "active": true
       |  }
    """.stripMargin
  }

}
