package data

import java.util.UUID

import _root_.data.tables.Merchant
import db.InMemoryDatabaseSetup
import play.api._
import play.api.inject.bind

import scala.concurrent.ExecutionContext.Implicits.global

class MerchantRepositorySpec extends InMemoryDatabaseSetup {

  override def fakeApplication(): Application = {
    RepositoryApplicationBuilder.fakeApplicationWithBinding(
      bind[MerchantRepository].to[MerchantRepositoryImpl])
  }

  "MerchantRepository" should {
    setupDB
    val repository: MerchantRepository = app.injector.instanceOf[MerchantRepository]

    "find merchant by id" in {
      val merchantId = UUID.randomUUID
      val merchant = Merchant(merchantId, "Merchant 1")
      insertMerchant(merchant)

      val merchantById = repository.findById(merchantId)

      merchantById.map(actualMerchandise => {
        assert(actualMerchandise == merchant)
      })
    }
  }

}
