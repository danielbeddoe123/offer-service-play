package data
import java.util.UUID

import com.google.inject.Singleton
import data.tables.merchants
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MerchantRepositoryImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends MerchantRepository with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def findById(id: UUID): Future[tables.Merchant] = db.run(merchants.filter(_.id === id).result.head)
}
