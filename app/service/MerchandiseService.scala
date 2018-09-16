package service

import java.util.UUID

import model.Merchandise

import scala.concurrent.Future

trait MerchandiseService {

  def fetchMerchandiseById(id: UUID): Future[Merchandise]
}
