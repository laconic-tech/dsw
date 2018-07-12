package co.lnic.dsw.api

import cats.effect._
import co.lnic.dsw.api.endpoints.UserEndpoint
import co.lnic.dsw.domain.interpreters._
import co.lnic.dsw.domain.interpreters.stores.MemoryStoreInterpreter
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    ServerStream.stream[IO]
}

object ServerStream {

  def stream[F[_]: Effect](implicit ec: ExecutionContext) = {
    // repos and services
    val dataStore = new MemoryStoreInterpreter[F]
    dataStore.createUser("seb", "seb", "seb@seb.com")

    val users = new UserAlgebraInterpreter[F](dataStore)
    val cluster = new ClusterAlgebraInterpreter[F]
    val apps = new ApplicationAlgebraInterpreter[F](dataStore, cluster)

    // endpoints
    val userEndpoint = new UserEndpoint[F](apps, users).service

    // server
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(userEndpoint, "/")
      .serve
  }
}
