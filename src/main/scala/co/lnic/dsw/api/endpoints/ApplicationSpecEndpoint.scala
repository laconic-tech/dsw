package co.lnic.dsw.api.endpoints

import cats.data._
import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._


import co.lnic.dsw.domain.algebras._

import scala.language.higherKinds

class ApplicationSpecEndpoint[F[_]: Effect](store: DataStoreAlgebra[F])
  extends Http4sDsl[F] {

  // custom encoders
  import co.lnic.dsw.api.Encoders._

  val service: HttpService[F] = HttpService[F] {
    case GET -> Root / "specs" => store.getApplicationSpecs().flatMap(specs => Ok(specs.asJson))
  }
}
