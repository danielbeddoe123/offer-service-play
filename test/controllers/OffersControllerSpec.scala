package controllers

import java.util.UUID

import org.scalatest.{Matchers, OptionValues}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.OfferService
import org.mockito.Mockito._

class OffersControllerSpec extends PlaySpec with Results with MockitoSugar with OptionValues {

  private val merchandiseId = UUID.randomUUID()

  "OffersController" should {
    "create an offer" in {
      val mockOfferService = mock[OfferService]
      val offerId = UUID.randomUUID
//      when(mockOfferService.createOffer()).thenReturn(offerId)

      val controller = new OfferController(stubControllerComponents(), mockOfferService)
      val path = s"/merchandise/$merchandiseId/offer"

      val result = controller.createOffer(merchandiseId).apply(FakeRequest(POST, path))
      status(result) mustBe play.api.http.Status.CREATED
      header("Location", result).value must include (s"/merchandise/$merchandiseId/offer/$offerId")
    }
  }
}
