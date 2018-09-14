package data

import java.util.UUID

import data.tables.{Merchandise, merchandises}
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MerchandiseRepositoryImpl @Inject()
  (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends MerchandiseRepository with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def findById(id: UUID): Future[Merchandise] = db.run(merchandises.filter(_.id === id).result.head)
}
