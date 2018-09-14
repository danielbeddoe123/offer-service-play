package service

import java.util.UUID

import model.Merchandise

trait MerchandiseService {

  def fetchMerchandiseById(id: UUID): Merchandise
}
