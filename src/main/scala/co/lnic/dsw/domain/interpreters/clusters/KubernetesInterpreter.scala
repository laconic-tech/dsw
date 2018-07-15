package co.lnic.dsw.domain.interpreters.clusters

import java.io.Closeable
import java.nio.file.Path
import java.util.zip.GZIPInputStream

import better.files.File
import cats._
import cats.data._
import cats.implicits._
import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain._
import hapi.chart.ChartOuterClass.Chart
import hapi.release.StatusOuterClass.Status
import hapi.services.tiller.Tiller.GetReleaseStatusRequest
import hapi.services.tiller.Tiller.InstallReleaseRequest
import hapi.services.tiller.Tiller.UninstallReleaseRequest
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import org.kamranzafar.jtar.TarInputStream
import org.microbean.helm.Tiller
import org.microbean.helm.chart.TapeArchiveChartLoader

import scala.util.Try

class KubernetesInterpreter[F[_]: Applicative] extends ClusterAlgebra[F] {

  // TODO: Move somewhere nicer
  def using[C <: Closeable, R](c: C)(f: C => R): R = try { f(c) } finally { c.close() }
  def tryUsing[R](c: => Closeable)(f: Closeable => R): Try[R] = Try(c).map(using(_)(f))

  // TODO: Move to try/either as these might fail
  def k8s[R](f: DefaultKubernetesClient => R): R = using(new DefaultKubernetesClient)(f(_))
  def tiller[R](f: Tiller => R): R = k8s(c => using(new Tiller(c))(f(_)))

  /**
    * Checks connectivity against the cluster implementation
    *
    * @return
    */
  override def getStatus(): F[ClusterStatus] =
    Try {
      // execute a command to see whether he have
      // connectivity to the kubernetes cluster or not
      k8s(_.getApiVersion)
      Online
    }
    .getOrElse(Offline)
    .pure[F]

  /**
    * Checks whether a namespace exists
    *
    * @param name
    * @return
    */
  override def hasNamespace(name: String): F[Boolean] = ???

  /**
    * Provision a new namespace
    *
    * @param name
    * @return
    */
  override def createNamespace(name: String): F[Unit] = ???

  /**
    * Check the status of a deployment
    *
    * @param name
    * @param namespace
    * @return
    */
  override def getDeploymentStatus(name: String, namespace: String): F[Status.Code] =
    tiller { t =>
      t.getReleaseServiceBlockingStub
       .getReleaseStatus(
         GetReleaseStatusRequest.newBuilder()
           .setName(name)
           .build()
       )
       .getInfo
       .getStatus
       .getCode
    }.pure[F]

  /**
    * Gets events for a particular pod
    *
    * @param podId
    * @return
    */
  override def getDeploymentEvents(podId: String): F[Any] = ???

  /**
    * Retrieve the logs for a given pod
    *
    * @param podId
    * @return
    */
  override def getLogs(podId: String): F[String] =
    k8s(_.pods.withName(podId).getLog(true)).pure[F]

  /**
    * Scale up or down the number of worker nodes in the cluster
    *
    * @param nodes
    * @return
    */
  override def scale(nodes: Int): F[Any] = ???

  /**
    * Returns the number of nodes in the cluster
    *
    * @return
    */
  override def getNodeCount(): F[Int] =
    k8s(_.nodes.list.getItems.size).pure[F]

  /**
    * Installs a new application in the cluster
    *
    * @param chart
    * @param name
    * @param namespace
    * @return
    */
  override def install(chart: Path, name: String, namespace: String): EitherT[F, String, Status.Code] = {
    // TODO: wrap this in Try/Either as they might fail for several reasons
    def loadTar(tar: TarInputStream): Chart.Builder = using(new TapeArchiveChartLoader())(_.load(tar))
    def load(path: Path): Chart.Builder = loadTar(new TarInputStream(File(path).newGzipInputStream()))

    EitherT {
      Try {
        tiller { t =>
          val chartBuilder = load(chart)

          // issue install command
          t.getReleaseServiceBlockingStub
           .installRelease(
             InstallReleaseRequest.newBuilder()
               .setName(name)
               .setNamespace(namespace)
               .setChart(chartBuilder)
               .setValues(chartBuilder.getValues)
               .build()
           )
            .getRelease
            .getInfo
            .getStatus
            .getCode
        }
      }.toEither.left.map(_.getMessage)
       .pure[F]
    }
  }

  /**
    * Kills an application
    *
    * @param name
    * @param namespace
    * @return
    */
  override def uninstall(name: String, namespace: String): EitherT[F, String, Boolean] =
    EitherT {
      Try {
        tiller { t =>
          t.getReleaseServiceBlockingStub
            .uninstallRelease(
              UninstallReleaseRequest.newBuilder()
                .setPurge(true)
                .setName(name)
                .build()
            )

          // no status returned by api
          true
        }
      }.toEither.left.map(_.getMessage)
        .pure[F]
    }
}
