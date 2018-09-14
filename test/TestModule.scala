import com.google.inject.AbstractModule
import data.{OfferRepository, OfferRepositoryImpl}
import org.scalatest.mockito.MockitoSugar

class TestModule extends AbstractModule {

//  val mockClient: Client = mock[Client]

  override def configure(): Unit = {
//    bind(classOf[OfferRepository]).toInstance(new OfferRepositoryImpl())
  }

}