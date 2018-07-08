package co.lnic.dsw.infra.helm

import better.files.File
import cats.data.EitherT
import cats.implicits._
import co.lnic.dsw.pimps.Futures.JavaFutureConverter
import hapi.chart.ChartOuterClass
import hapi.release.StatusOuterClass.Status.Code
import hapi.services.tiller.Tiller.GetReleaseStatusRequest
import hapi.services.tiller.Tiller.InstallReleaseRequest
import hapi.services.tiller.Tiller.InstallReleaseResponse
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import org.kamranzafar.jtar.TarInputStream
import org.microbean.helm._
import org.microbean.helm.chart.DirectoryChartLoader
import org.microbean.helm.chart.TapeArchiveChartLoader

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try


sealed trait DeploymentStatus
case object Unknown extends DeploymentStatus
case object InProgress extends DeploymentStatus
case object Success extends DeploymentStatus
case object Decomissioned extends DeploymentStatus

trait ChartAlgebra[F[_]] {

  def install(name: String,
              chart: String,
              namespace: String,
              values: Map[String, String],
              dryRun: Boolean = false): EitherT[F, String, InstallReleaseResponse]

  def status(name: String): EitherT[F, String, DeploymentStatus]
  def delete(name: String): EitherT[F, String, Success.type]
}

class ChartInterpreter(implicit ec: ExecutionContext) extends ChartAlgebra[Future] {

  def runJ[R](f: ReleaseManager => java.util.concurrent.Future[R]) = {
    run(rm => f(rm).toScala())
  }

  def run[R](f: ReleaseManager => Future[R]): EitherT[Future, String, R] = {
    EitherT {
      Try {
        val client = new DefaultKubernetesClient()
        val tiller = new Tiller(client)
        val rm = new ReleaseManager(tiller)

        try {
          f(rm)
        }
        finally {
          rm.close()
        }
      }.toEither
        .left
        .map(_.getMessage)
        .left.map(Future.successful)
        .bisequence
      }
    }

  private def loadChart(path: String): EitherT[Future, String, ChartOuterClass.Chart.Builder] =
    EitherT {
      val r = Try {
        val tar = new TarInputStream(File(path).newGzipInputStream())
        val loader = new TapeArchiveChartLoader()
        // load the chart from the given url
        loader.load(tar)
      }.toEither
        .left
        .map(_.getMessage)

      r.pure[Future]
    }


  override def install(name: String,
                       chart: String,
                       namespace: String,
                       values: Map[String, String],
                       dryRun: Boolean): EitherT[Future, String, InstallReleaseResponse] = {

    // install request
    val req = InstallReleaseRequest
      .newBuilder()
      .setName(name)
      .setNamespace(namespace)
      .setDryRun(dryRun)

    for {
      release <- loadChart(chart)
      result <- runJ(_.install(req, release))
    } yield result
  }

  override def status(name: String): EitherT[Future, String, DeploymentStatus] = {
    val req = GetReleaseStatusRequest
      .newBuilder()
      .setName(name)
      .build()

    run { helm =>
      helm.getStatus(req)
        .toScala()
        .map(status => status.getInfo.getStatus.getCode)
        .map {
          case Code.DEPLOYED => Success
          case Code.DELETED | Code.FAILED => Decomissioned
          case Code.PENDING_INSTALL | Code.PENDING_UPGRADE => InProgress
          case _ => Unknown
        }
        .recover {
          case _ => Unknown
        }
    }
  }

  override def delete(name: String): EitherT[Future, String, Success.type] = ???
}