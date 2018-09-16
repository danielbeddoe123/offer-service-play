package data

import java.util.UUID

import scala.concurrent.Future

trait Repository[T] {

  def findById(id: UUID): Future[T]
}
