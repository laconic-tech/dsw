package co.lnic.dsw.api.endpoints

import cats.data._
import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.authentication.BasicAuth
import co.lnic.dsw.api.adts._
import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain._
import org.http4s.server.middleware.authentication.BasicAuth.BasicAuthenticator


class ApplicationEndpoint[F[_]: Effect](applications: ApplicationAlgebra[F], store: DataStoreAlgebra[F])
  extends Http4sDsl[F] {

  implicit val createApplicationDecoder: EntityDecoder[F, CreateApplicationRequest] = jsonOf[F, CreateApplicationRequest]

  val authStore: BasicAuthenticator[F, User] = (creds: BasicCredentials) => store.getUserById(creds.username).value
  val auth: AuthMiddleware[F, User] = BasicAuth("dsw", authStore)

  val service: HttpService[F] = auth(AuthedService[User, F] {

    case GET -> Root / "applications" / name as user =>
      // get the application by it's id and return
      // - the current state
      // - exposed http services and where to go to get them
      Ok()

    case POST -> Root / "applications" / name / "start" as user =>

      //      applications.provision(name)
      Ok()

    case POST -> Root / "applications" / name / "stop" as user =>
      Ok()

    case req @ POST -> Root / "applications" / name / "share" as user =>
      // share an application with another user/users
      Ok()

    case authRequest @ POST -> Root / "applications" as user =>
      // we receive the id of the spec
      // and add the application to the user
      val result = for {
        create <- EitherT.liftF(authRequest.req.as[CreateApplicationRequest])
        spec <- EitherT.fromOptionF(store.getApplicationSpecBy(create.specId).value, "Application Spec not found")
        response <- applications.provision(create.name, spec, user)
      } yield response

      result.value.flatMap {
        case Left(error) => BadRequest(error)
        case Right(response) => Ok(response.asJson)
      }
  })
}
