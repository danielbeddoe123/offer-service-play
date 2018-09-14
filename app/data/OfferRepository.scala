package data

import data.tables.Offer

import scala.concurrent.Future

trait OfferRepository {

  def findAll(): Future[Seq[Offer]]

  def create(offer: Offer): Future[Int]
}
