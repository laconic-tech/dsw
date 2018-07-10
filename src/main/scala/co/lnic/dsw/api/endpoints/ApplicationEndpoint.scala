package co.lnic.dsw.api.endpoints

import cats.data._
import cats.effect._
import cats.implicits._

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl

import co.lnic.dsw.api.adts._
import co.lnic.dsw.domain.algebras._


class ApplicationEndpoint[F[_]: Effect](applications: ApplicationAlgebra[F], store: DataStoreAlgebra[F])
  extends Http4sDsl[F] {

  val service = HttpService[F] {

    case GET -> Root / "applications" / name =>
      // get the application by it's id and return
      // - the current state
      // - exposed http services and where to go to get them
      Ok()

    case POST -> Root / "applications" / name / "start" =>

//      applications.provision(name)
      Ok()

    case POST -> Root / "applications" / name / "stop" =>
      Ok()

    case req @ POST -> Root / "applications" / name / "share" =>
      // share an application with another user/users
      Ok()

    case req @ POST -> Root / "applications" =>
      // we receive the id of the spec
      // and add the application to the user
      val userId = "admin" // TODO: get from auth

      val result = for {
        create <- EitherT.liftF(req.as[CreateApplicationRequest])
        user <- EitherT.fromOptionF(store.getUserById(userId).value, "User not found")
        spec <- EitherT.fromOptionF(store.getApplicationSpecBy(create.specId).value, "Application Spec not found")
        response <- applications.provision(create.name, spec, user)
      } yield Ok(response.asJson)

      result.value.flatMap {
        case Left(error) => BadRequest(error)
        case Right(response) => Ok(response)
      }
  }
}
