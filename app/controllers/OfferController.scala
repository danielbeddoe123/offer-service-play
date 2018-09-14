package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.google.common.net.HttpHeaders
import javax.inject.Inject
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc._
import service.{MerchandiseService, OfferService}
import json.ReadFormats._
import model.Offer

class OfferController @Inject()(cc: ControllerComponents,
                                offerService: OfferService,
                                merchandiseService: MerchandiseService) extends AbstractController(cc) {

  def createOffer(merchandiseId: UUID) = Action { implicit request: Request[AnyContent] =>
    Json.fromJson[OfferResource](request.body.asJson.get) match {
      case JsSuccess(resource: OfferResource, _) =>
        val offerId = offerService.createOffer(createOfferFromResource(resource, merchandiseId))
        Created.withHeaders((HttpHeaders.LOCATION, createdOfferUrl(merchandiseId, offerId)))
      case _ =>
        BadRequest
    }
  }

  private def createdOfferUrl(merchandiseId: UUID, offerId: UUID)(implicit request: RequestHeader): String = {
    routes.OfferController.createOffer(merchandiseId).absoluteURL().concat(s"/$offerId")
  }

  private def createOfferFromResource(resource: OfferResource, merchandiseId: UUID): Offer = {
    val expiryDate = LocalDate.parse(resource.expiryDate, DateTimeFormatter.ISO_LOCAL_DATE)
    Offer(merchandiseService.fetchMerchandiseById(merchandiseId), expiryDate, resource.description, resource.price, resource.active)
  }
}
