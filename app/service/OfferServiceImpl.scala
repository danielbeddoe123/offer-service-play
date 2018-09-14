package service
import java.util.UUID

import data.{OfferRepository, OfferRepositoryImpl}
import data.tables.Offer
import javax.inject.Inject
import model.{Offer => ModelOffer}

class OfferServiceImpl @Inject()(val offerRepository: OfferRepository) extends OfferService {
  override def createOffer(offer: ModelOffer): UUID = {
    val offerId = UUID.randomUUID
    offerRepository.create(
      Offer(offerId, offer.description, offer.merchandise.id,
        offer.price.currency.getCurrencyCode, offer.price.amount,
        offer.active, offer.expiryDate))
    offerId
  }
}
