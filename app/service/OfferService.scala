package service

import java.util.UUID

import model.Offer

trait OfferService {

  def createOffer(offer: Offer): UUID
}
