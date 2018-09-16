package data

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tables.{Offer, offers}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OfferRepositoryImpl @Inject()
  (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends OfferRepository with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def create(offer: Offer): Future[Int] = db.run(offers += offer)

}
