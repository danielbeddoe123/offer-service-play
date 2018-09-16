package integration

import java.time.LocalDate
import java.util.Currency.getInstance
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import java.util.{Currency, UUID}

import com.google.inject.Singleton
import data.tables.{Merchandise, Merchant}
import data._
import db.InMemoryDatabaseSetup
import javax.inject.Inject
import model.{Offer, Price, Merchandise => ModelMerchandise, Merchant => ModelMerchant}
import org.awaitility.Awaitility
import org.scalatest.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.{Application, Mode}
import service.{MerchandiseService, MerchandiseServiceImpl, OfferService, OfferServiceImpl}
import utils.DataRequestFactory


class OffersIntegrationSpec extends InMemoryDatabaseSetup with MockitoSugar {

  private val offerId = UUID.randomUUID

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .bindings(bind[OfferService].to[MockedOfferService])
      .bindings(bind[OfferRepository].to[OfferRepositoryImpl])
      .bindings(bind[MerchandiseService].to[MerchandiseServiceImpl])
      .bindings(bind[MerchandiseRepository].to[MerchandiseRepositoryImpl])
      .bindings(bind[MerchantRepository].to[MerchantRepositoryImpl])
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
      val description = "Some description"
      val expiryDate = LocalDate.of(2018, 12, 31)
      val currencyCode = "GBP"
      val priceAmount = BigDecimal(20.00)
      val active = true
      val offer = Offer(ModelMerchandise(merchandiseId, ModelMerchant(merchantId)), expiryDate, description, Price(getInstance(currencyCode), priceAmount), active)
      val json = DataRequestFactory.generateJson(offer)

      val response = await(request.post(json))

      response.status mustBe CREATED
      response.header("Location").value mustBe s"$createOfferUrl/$offerId"

      Awaitility.await().atMost(5, SECONDS).pollDelay(1, SECONDS).until(() => {
        val size = fetchOffers().size
        size == 1
      })

      val createdOffer = fetchOffers().head

      assert(createdOffer.active)
      assert(createdOffer.description == description)
      assert(createdOffer.price == priceAmount)
      assert(createdOffer.currency == currencyCode)
      assert(createdOffer.merchandiseId == merchandiseId)
      assert(createdOffer.expiryDate == expiryDate)
    }
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


