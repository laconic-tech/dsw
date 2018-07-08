package co.lnic.dsw.pimps

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Futures {

  implicit class JavaFutureConverter[T](val f: java.util.concurrent.Future[T]) extends AnyVal {

    def toScala()(implicit ec: ExecutionContext): Future[T] = Future {
        f.get() // this blocks - is there any other way?
    }
  }

}
