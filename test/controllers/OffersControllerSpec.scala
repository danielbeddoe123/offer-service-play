package controllers

import java.time.LocalDate
import java.util.{Currency, UUID}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import model.{Merchandise, Offer, Price}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.OptionValues
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.{MerchandiseService, OfferService}
import utils.DataRequestFactory

class OffersControllerSpec extends PlaySpec with MockitoSugar with OptionValues {

  private val merchandiseId = UUID.randomUUID()

  implicit val sys = ActorSystem("OffersControllerSpec")
  implicit val mat = ActorMaterializer()

  "OffersController" should {
    "create an offer" in {
      val mockOfferService = mock[OfferService]
      val mockMerchandiseService = mock[MerchandiseService]
      val offerId = UUID.randomUUID
      val merchandise = mock[Merchandise]
      val offer = Offer(merchandise, LocalDate.now(), "Some description", Price(Currency.getInstance("GBP"), BigDecimal(20.00)), active = true)
      when(mockOfferService.createOffer(offer)).thenReturn(offerId)
      when(mockMerchandiseService.fetchMerchandiseById(merchandiseId)).thenReturn(merchandise)

      val controller = new OfferController(stubControllerComponents(), mockOfferService, mockMerchandiseService)
      val path = s"/merchandise/$merchandiseId/offer"

      val fakeRequest = FakeRequest(POST, path)
        .withJsonBody(Json.parse(DataRequestFactory.generateJson(offer)))
      val result = controller.createOffer(merchandiseId)(fakeRequest)


      status(result) mustBe play.api.http.Status.CREATED
      header("Location", result).value must include (s"/merchandise/$merchandiseId/offer/$offerId")
    }
  }
}
