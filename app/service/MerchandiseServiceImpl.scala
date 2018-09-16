package service

import java.util.UUID

import data.{MerchandiseRepository, MerchantRepository}
import javax.inject.Inject
import model.{Merchandise, Merchant}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MerchandiseServiceImpl @Inject()(val merchandiseRepository: MerchandiseRepository, val merchantRepository: MerchantRepository) extends MerchandiseService {
  override def fetchMerchandiseById(id: UUID): Future[Merchandise] = {
    merchandiseRepository.findById(id).map(merchandise => {
      Merchandise(id, Merchant(merchandise.merchantId))
    })
  }
}
