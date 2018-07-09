package co.lnic.dsw.api

import cats.implicits._
import cats.effect._
import cats.data._

import co.lnic.dsw.domain.algebras._

import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl

import org.http4s.circe._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.language.higherKinds

class UserEndpoint[F[_]: Effect](applications: ApplicationAlgebra[F], users: UserAlgebra[F])
  extends Http4sDsl[F] {

  val service: HttpService[F] = HttpService[F] {
    case GET -> Root/ "users" / name / "applications" =>
      val result = for {
        user <- users.byId(name)
        apps <- EitherT.right[String](applications.byUser(user))
      } yield apps

      result.value.flatMap {
        case Left(msg) => BadRequest(msg)
        case Right(apps) => Ok(apps.asJson)
      }
  }
}