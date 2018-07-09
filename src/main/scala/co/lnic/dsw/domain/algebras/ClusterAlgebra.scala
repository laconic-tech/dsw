package co.lnic.dsw.domain.algebras

import java.nio.file.Path

import cats.data._
import co.lnic.dsw.domain.domain.ClusterStatus

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
  def getDeploymentStatus(name: String, namespace: String): F[Any]

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
  def install(chart: Path, name: String, namespace: String): EitherT[F, String, Boolean]

  /**
    * Kills an application
    * @param name
    * @param namespace
    * @return
    */
  def kill(name: String, namespace: String): EitherT[F, String, Boolean]
}
