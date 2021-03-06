package co.lnic.dsw.api

import better.files.File
import cats.effect._
import co.lnic.dsw.api.endpoints._
import co.lnic.dsw.api.support._
import co.lnic.dsw.domain._
import co.lnic.dsw.domain.domain._
import co.lnic.dsw.domain.interpreters._
import co.lnic.dsw.domain.interpreters.stores._
import co.lnic.dsw.domain.interpreters.clusters._
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
    val http = new Http[F]()
    val dataStore = new MemoryStoreInterpreter[F]
    dataStore.createUser("seb", "seb", "seb@seb.com")
    dataStore.createApplicationSpec(
      "jupyter",
      File("infra/charts/jupyter-0.7.3.1.tgz").path,
      Seq(
        ExposedService("jupyter", "Jupyter Notebook", None, domain.Http, 8888)
      )
    )

    val users = new UserAlgebraInterpreter[F](dataStore)
    val cluster = new KubernetesInterpreter[F]
    val apps = new ApplicationAlgebraInterpreter[F](dataStore, cluster)

    // endpoints
    val userEndpoint = new UserEndpoint[F](apps, users).service
    val specsEndpoint = new ApplicationSpecEndpoint[F](dataStore).service
    val applicationsEndpoint = new ApplicationEndpoint[F](apps, dataStore, http).service

    // server
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(userEndpoint, "/")
      .mountService(applicationsEndpoint, "/")
      .mountService(specsEndpoint, "/")
      .serve
  }
}
