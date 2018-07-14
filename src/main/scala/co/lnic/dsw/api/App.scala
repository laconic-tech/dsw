package co.lnic.dsw.api

import better.files.File
import cats.effect._
import co.lnic.dsw.api.endpoints.ApplicationEndpoint
import co.lnic.dsw.api.endpoints.UserEndpoint
import co.lnic.dsw.domain.domain
import co.lnic.dsw.domain.domain.ExposedService
import co.lnic.dsw.domain.interpreters._
import co.lnic.dsw.domain.interpreters.stores.MemoryStoreInterpreter
import com.google.api.Http
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
    dataStore.createApplicationSpec(
      "jupyter",
      File("infra/charts/jupyter-0.7.3.tgz").path,
      Seq(
        ExposedService("jupyter", "Jupyter Notebook", None, domain.Http, 80)
      )
    )

    val users = new UserAlgebraInterpreter[F](dataStore)
    val cluster = new ClusterAlgebraInterpreter[F]
    val apps = new ApplicationAlgebraInterpreter[F](dataStore, cluster)

    // endpoints
    val userEndpoint = new UserEndpoint[F](apps, users).service
    val applicationsEndpoint = new ApplicationEndpoint[F](apps, dataStore).service

    // server
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(userEndpoint, "/")
      .mountService(applicationsEndpoint, "/")
      .serve
  }
}
