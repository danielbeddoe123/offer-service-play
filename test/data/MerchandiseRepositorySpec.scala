package data

import java.util.UUID

import db.InMemoryDatabaseSetup
import _root_.data.tables.{Merchandise, Merchant}
import org.scalatest.mockito.MockitoSugar
import play.api._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import service.OfferService

import scala.concurrent.ExecutionContext.Implicits.global

class MerchandiseRepositorySpec extends InMemoryDatabaseSetup {

  override def fakeApplication(): Application = {
    RepositoryApplicationBuilder.fakeApplicationWithBinding(
      bind[MerchandiseRepository].to[MerchandiseRepositoryImpl])
  }

  "MerchandiseRepository" should {
    setupDB
    val repository: MerchandiseRepository = app.injector.instanceOf[MerchandiseRepositoryImpl]

    "find merchandise by id" in {
      val merchantId = UUID.randomUUID
      val merchandiseId = UUID.randomUUID
      insertMerchant(Merchant(merchantId, "Merchant 1"))
      val merchandise = Merchandise(merchandiseId, "PRODUCT", merchantId)
      insertMerchandise(merchandise)

      val allMerchandise = repository.findById(merchandiseId)

      allMerchandise.map(actualMerchandise => {
        assert(actualMerchandise == merchandise)
      })
    }
  }

}
