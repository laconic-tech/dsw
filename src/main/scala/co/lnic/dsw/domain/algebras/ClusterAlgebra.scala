package co.lnic.dsw.domain.algebras

import java.nio.file.Path

import cats.data._
import cats.effect.IO
import co.lnic.dsw.domain.domain.ClusterStatus
import co.lnic.dsw.domain.domain.Port
import hapi.release.StatusOuterClass.Status

trait ClusterAlgebra[F[_]] {

  /**
    * Checks connectivity against the cluster implementation
    * @return
    */
  def getStatus(): F[ClusterStatus]

  /**
    * Checks whether a namespace exists
    * @param name
    * @return
    */
  def hasNamespace(name: String): F[Boolean]

  /**
    * Provision a new namespace
    * @param name
    * @return
    */
  def createNamespace(name: String): F[Unit]

  /**
    * Check the status of a deployment
    * @param name
    * @param namespace
    * @return
    */
  // NOTE: Extend to return a more comprehensive status report?
  def getDeploymentStatus(name: String, namespace: String): F[Status.Code]

  /**
    * Gets events for a particular pod
    * @param podId
    * @return
    */
  def getDeploymentEvents(podId: String): F[Any]

  /**
    * Retrieve the logs for a given pod
    * @param podId
    * @return
    */
  def getLogs(podId: String): F[String]

  /**
    * Scale up or down the number of worker nodes in the cluster
    * @param nodes
    * @return
    */
  def scale(nodes: Int): F[Any]

  /**
    * Returns the number of nodes in the cluster
    * @return
    */
  def getNodeCount(): F[Int]


  /**
    * Installs a new application in the cluster
    * @param chart
    * @param name
    * @param namespace
    * @return
    */
  def install(chart: Path, name: String, namespace: String): EitherT[F, String, Status.Code]

  /**
    * Kills an application
    * @param name
    * @param namespace
    * @return
    */
  def uninstall(name: String, namespace: String): EitherT[F, String, Boolean]

  /**
    * Fetches the url for a service
    * @param applicationName
    * @param serviceName
    * @param namespace
    * @param servicePort
    * @return
    */
  def getServiceUrl(applicationName: String, serviceName: String, namespace: String, servicePort: Port): F[String]
}
