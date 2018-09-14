package models

import java.util.UUID

import org.scalatest.{MustMatchers, WordSpec}
import model._

class ModelsSpec extends WordSpec with MustMatchers {

  "Two products" should {
    "be equal" in {
      val productId = UUID.randomUUID
      val merchantId = UUID.randomUUID
      val product1 = new Product(productId, Merchant(merchantId))
      val product2 = new Product(productId, Merchant(merchantId))

      product1 mustEqual product2
    }
  }

}
