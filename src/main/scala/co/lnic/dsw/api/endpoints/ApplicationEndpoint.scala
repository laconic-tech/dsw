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
import org.http4s.server.middleware.authentication.BasicAuth.BasicAuthenticator
import co.lnic.dsw.api._
import co.lnic.dsw.api.adts._
import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain._
import org.http4s.blaze.http.http_parser.Http1ClientParser


class ApplicationEndpoint[F[_]: Effect](applications: ApplicationAlgebra[F], store: DataStoreAlgebra[F])
  extends Http4sDsl[F] {

  implicit val createApplicationDecoder: EntityDecoder[F, CreateApplicationRequest] = jsonOf[F, CreateApplicationRequest]

  val authStore: BasicAuthenticator[F, User] = (creds: BasicCredentials) => store.getUserById(creds.username).value
  val auth: AuthMiddleware[F, User] = BasicAuth("dsw", authStore)

  val service: HttpService[F] = auth(AuthedService[User, F] {

    case GET -> Root / "applications" as user =>
      for {
        apps <- applications.byUser(user)
        resp <- Ok(apps.asJson)
      } yield resp

    case GET -> Root / "applications" / ApplicationIdVar(appId) as user =>
      // get the application by it's id and return
      // - the current state
      // - exposed http services and where to go to get them
      applications.byId(appId).value.flatMap {
        case Some(app) => Ok(app.asJson)
        case None => NotFound()
      }

    case authedReq @ GET -> Root / "applications" / ApplicationIdVar(appId) / "services" / serviceName as user =>
        NotImplemented()
//        case req =>
//          if(verifyRequest(req)) {
//            for {
//              client <- Http1Client[IO]()
//              newHost = "host2"
//              newAuthority = Authority(host = RegName("host2"), port = Some(80))
//              proxiedReq =
//
//            } yield response
//          } else {
//            Forbidden("Some forbidden message...")
//          }
//      }

    case authRequest @ POST -> Root / "applications" / name / "start" as user =>
      NotImplemented()

    case authRequest @ POST -> Root / "applications" / name / "stop" as user =>
      NotImplemented()

    case authRequest @ POST -> Root / "applications" / name / "share" as user =>
      // share an application with another user/users
      NotImplemented()

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
