package data

// Use H2Profile to connect to an H2 database
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

  def findAll(): Future[Seq[Offer]] = db.run(offers.result)
  def create(offer: Offer): Future[Int] = db.run(offers += offer)

}
