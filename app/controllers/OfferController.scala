package controllers

import java.util.UUID

import com.google.common.net.HttpHeaders
import javax.inject.Inject
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc._
import service.OfferService
import json.ReadFormats._
import model.Offer

class OfferController @Inject()(cc: ControllerComponents, offerService: OfferService) extends AbstractController(cc) {

  def createOffer(merchandiseId: UUID) = Action { implicit request: Request[AnyContent] =>
    Json.fromJson[OfferResource](request.body.asJson.get) match {
      case JsSuccess(resource: OfferResource, _) =>
        val offerId = offerService.createOffer(createOfferFromResource(resource))
        Created.withHeaders((HttpHeaders.LOCATION, createdOfferUrl(merchandiseId, offerId)))
      case _ =>
        BadRequest
    }
  }

  private def createdOfferUrl(merchandiseId: UUID, offerId: UUID)(implicit request: RequestHeader): String = {
    routes.OfferController.createOffer(merchandiseId).absoluteURL().concat(s"/$offerId")
  }

  private def createOfferFromResource(resource: OfferResource): Offer = {
    Offer(null, null, resource.description, resource.price, resource.active)
  }
}
