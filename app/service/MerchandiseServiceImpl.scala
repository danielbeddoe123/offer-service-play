package service

import java.util.UUID

import data.MerchandiseRepository
import javax.inject.Inject
import model.Merchandise

class MerchandiseServiceImpl @Inject()(val merchandiseRepository: MerchandiseRepository) extends MerchandiseService {
  override def fetchMerchandiseById(id: UUID): Merchandise = {
    val merchandise = merchandiseRepository.findById(id)
    Merchandise(id, null)
  }
}
