package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.google.common.net.HttpHeaders
import controllers.json.ReadFormats._
import javax.inject.Inject
import model.Offer
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc._
import service.{MerchandiseService, OfferService}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class OfferController @Inject()(cc: ControllerComponents,
                                offerService: OfferService,
                                merchandiseService: MerchandiseService) extends AbstractController(cc) {

  def createOffer(merchandiseId: UUID) = Action.async(parse.json) { implicit request =>
    Json.fromJson[OfferResource](request.body) match {
      case JsSuccess(resource: OfferResource, _) =>
        createOfferFromResource(resource, merchandiseId).map(offer => {
          val offerId = offerService.createOffer(offer)
          Created.withHeaders((HttpHeaders.LOCATION, createdOfferUrl(merchandiseId, offerId)))
        }).recover {
          case _: MerchandiseNotFoundException => NotFound
          case _ => InternalServerError
        }
      case _ => Future(BadRequest)
    }
  }

  private def createdOfferUrl(merchandiseId: UUID, offerId: UUID)(implicit request: RequestHeader): String = {
    routes.OfferController.createOffer(merchandiseId).absoluteURL().concat(s"/$offerId")
  }

  private def createOfferFromResource(resource: OfferResource, merchandiseId: UUID): Future[Offer] = {
    val expiryDate = LocalDate.parse(resource.expiryDate, DateTimeFormatter.ISO_LOCAL_DATE)
    val merchandise = merchandiseService.fetchMerchandiseById(merchandiseId)
    merchandise.map(m =>
      Offer(m, expiryDate, resource.description, resource.price, resource.active))
      .recover {
        case _ => throw MerchandiseNotFoundException(s"Cannot find merchandise by id: $merchandiseId")
      }
  }
}
