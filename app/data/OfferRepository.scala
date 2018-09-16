package data

import data.tables.Offer

import scala.concurrent.Future

trait OfferRepository {

  def create(offer: Offer): Future[Int]
}
