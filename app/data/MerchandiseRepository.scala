package data

import java.util.UUID

import data.tables.Merchandise

import scala.concurrent.Future

trait MerchandiseRepository {

  def findById(id: UUID): Future[Merchandise]
}
