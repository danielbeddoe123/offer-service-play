package data

import org.scalatest.mockito.MockitoSugar
import play.api.{Application, Mode}
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import service.{MerchandiseService, OfferService}

object RepositoryApplicationBuilder extends MockitoSugar {

  def fakeApplicationWithBinding(bindModule: GuiceableModule): Application = {
    new GuiceApplicationBuilder()
      .bindings(bind[OfferService].toInstance(mock[OfferService]))
      .bindings(bind[MerchandiseService].toInstance(mock[MerchandiseService]))
      .bindings(bindModule)
      .in(Mode.Test)
      .build()
  }
}
