package co.lnic.dsw.infra.helm

import java.net.URI

import cats.effect._
import hapi.chart.ChartOuterClass
import hapi.services.tiller.Tiller.{GetReleaseStatusRequest, InstallReleaseRequest}
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import org.microbean.helm._
import org.microbean.helm.chart.URLChartLoader

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class DeploymentStatus(description: String)
case object Success

trait ChartAlgebra[F[_]] {

  def install(name: String,
              chart: String,
              namespace: String,
              values: Map[String, String])
             (dryRun: Boolean = false): F[Any]

  def status(name: String): F[DeploymentStatus]
  def delete(name: String): F[Either[String, Success.type]]
}

class ChartInterpreter(implicit ec: ExecutionContext) extends ChartAlgebra[Future] {

  private def getTiller: Either[String, ReleaseManager] = {
    Try {
      val client = new DefaultKubernetesClient()
      val tiller = new Tiller(client)
      new ReleaseManager(tiller)
    }.toEither
      .left
      .map(_.getMessage)
  }

  private def loadChart(chartUrl: String): Either[String, ChartOuterClass.Chart.Builder] = {
    Try {
      val url = URI.create(chartUrl).toURL()
      val loader = new URLChartLoader()
      // load the chart from the given url
      loader.load(url)
    }.toEither
      .left
      .map(_.getMessage)
  }

  private def fromJavaFuture[T](f: java.util.concurrent.Future[T]): IO[T] = {
    IO.fromFuture {
      IO {
        Future {
          f.get()
        }
      }
    }
  }

  override def install(name: String,
                       chart: String,
                       namespace: String,
                       values: Map[String, String])(dryRun: Boolean): Future[Any] = {

    // install request
    val req = InstallReleaseRequest
      .newBuilder()
      .setName(name)
      .setNamespace(namespace)
      .setDryRun(dryRun)

    for {
      chart  <- loadChart(chart)
      client <- getTiller
      result <- fromJavaFuture(client.install(req, chart))
    } yield result
  }

  override def status(name: String): Future[DeploymentStatus] = {
    val req = GetReleaseStatusRequest
      .newBuilder()
      .setName(name)
      .build()

    for {
      tiller <- getTiller
      result <- fromJavaFuture(tiller.getStatus(req))
    } yield DeploymentStatus(result.getInfo.getStatus.getCode.name())
  }

  override def delete(name: String): Future[Either[String, Success.type]] = ???
}