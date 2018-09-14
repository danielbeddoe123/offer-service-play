package integration

import java.time.LocalDate
import java.util.UUID

import com.google.inject.Singleton
import data.tables.{Merchandise, Merchant}
import data.{MerchandiseRepository, MerchandiseRepositoryImpl, OfferRepository, OfferRepositoryImpl}
import db.InMemoryDatabaseSetup
import javax.inject.Inject
import model.Offer
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.{Application, Mode}
import service.{MerchandiseService, MerchandiseServiceImpl, OfferService, OfferServiceImpl}


class OffersIntegrationSpec extends InMemoryDatabaseSetup with MockitoSugar {

  private val offerId = UUID.randomUUID

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .bindings(bind[OfferService].to[MockedOfferService])
      .bindings(bind[OfferRepository].to[OfferRepositoryImpl])
      .bindings(bind[MerchandiseService].to[MerchandiseServiceImpl])
      .bindings(bind[MerchandiseRepository].to[MerchandiseRepositoryImpl])
      .in(Mode.Test)
      .build()
  }

  private val merchandiseId = UUID.randomUUID
  private val merchantId = UUID.randomUUID

  private val wsClient = app.injector.instanceOf[WSClient]
  "submitting a POST request to the offer endpoint" should {
    setupDB

    "create an offer" in {
      val mockedOfferService = app.injector.instanceOf[MockedOfferService]
      mockedOfferService.offerId = offerId

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
      val createdOffer = fetchOffers().head

      assert(createdOffer.active)
      assert(createdOffer.description == "Some description")
      assert(createdOffer.price == BigDecimal(20.00))
      assert(createdOffer.currency == "GBP")
      assert(createdOffer.merchandiseId == merchandiseId)
      assert(createdOffer.expiryDate == LocalDate.of(2018, 12, 31))
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

@Singleton
class MockedOfferService @Inject()(override val offerRepository: OfferRepository) extends OfferServiceImpl(offerRepository) {
  var offerId: UUID = _

  override def createOffer(offer: Offer): UUID = {
    super.createOffer(offer)
    offerId
  }
}


