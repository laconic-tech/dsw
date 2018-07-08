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

  type ChartBuilder = ChartOuterClass.Chart.Builder
  type InstallChart = ChartBuilder => InstallReleaseRequest

  def run[R](f: Tiller => R): Either[String, R] =
    Try {
      val client = new DefaultKubernetesClient()
      val tiller = new Tiller(client)

      try {
        f(tiller)
      }
      finally {
        tiller.close()
        client.close()
      }
    }.toEither
     .left.map(_.getMessage)

  private def loadChart(path: String): Either[String, ChartBuilder] =
      Try {
        val tar = new TarInputStream(File(path).newGzipInputStream())
        new TapeArchiveChartLoader()
          .load(tar)
      }.toEither
       .left
       .map(_.getMessage)

  override def install(name: String,
                       chart: String,
                       namespace: String,
                       values: Map[String, String],
                       dryRun: Boolean): EitherT[Future, String, InstallReleaseResponse] = {

    // install request helper func
    val prepare: InstallChart = b => InstallReleaseRequest
        .newBuilder()
        .setName(name)
        .setNamespace(namespace)
        .setDryRun(dryRun)
        .setChart(b)
        .setValues(b.getValues)
        .build()

    EitherT {
      Future {
        for {
          release <- loadChart(chart)
          response <- run(tiller => tiller.getReleaseServiceBlockingStub.installRelease(prepare(release)))
        } yield response
      }
    }
  }

  override def status(name: String): EitherT[Future, String, DeploymentStatus] = {
    val request = GetReleaseStatusRequest
      .newBuilder()
      .setName(name)
      .build()

    EitherT {
      Future {
        run { tiller =>
          val response = tiller.getReleaseServiceBlockingStub.getReleaseStatus(request)

          response.getInfo.getStatus.getCode match {
            case Code.DEPLOYED => Success
            case Code.DELETED | Code.FAILED => Decomissioned
            case Code.PENDING_INSTALL | Code.PENDING_UPGRADE => InProgress
            case _ => Unknown
          }
        }
      }.recover {
        case _ => Right(Unknown)
      }
    }
  }

  override def delete(name: String): EitherT[Future, String, Success.type] = ???
}