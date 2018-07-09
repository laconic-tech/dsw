package co.lnic.dsw.domain.interpreters

import java.nio.file.Path

import cats.data.EitherT
import co.lnic.dsw.domain.algebras.ClusterAlgebra
import co.lnic.dsw.domain.domain

import scala.language.higherKinds

class ClusterAlgebraInterpreter[F[_]] extends ClusterAlgebra[F] {

  /**
    * Checks connectivity against the cluster implementation
    *
    * @return
    */
  override def getStatus(): F[domain.ClusterStatus] = ???

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
  override def getDeploymentStatus(name: String, namespace: String): F[Any] = ???

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
  override def getNodeCount(): F[Int] = ???

  /**
    * Installs a new application in the cluster
    *
    * @param chart
    * @param name
    * @param namespace
    * @return
    */
  override def install(chart: Path, name: String, namespace: String): EitherT[F, String, Boolean] = ???

  /**
    * Kills an application
    *
    * @param name
    * @param namespace
    * @return
    */
  override def kill(name: String, namespace: String): EitherT[F, String, Boolean] = ???
}
