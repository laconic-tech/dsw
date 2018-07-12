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

class UserEndpoint[F[_]: Effect](applications: ApplicationAlgebra[F], users: UserAlgebra[F])
  extends Http4sDsl[F] {

  val service: HttpService[F] = HttpService[F] {

    case GET -> Root/ "users" / name / "applications" =>
      val result = for {
        user <- users.byId(name)
        apps <- OptionT.liftF(applications.byUser(user))
      } yield apps

      result.value.flatMap {
        case Some(apps) => Ok(apps.asJson)
        case None => NotFound()
      }
  }
}
