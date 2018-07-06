package co.lnic.dsw.domain.clusters

trait ClusterAlgebra[F[_]] {

  def get(name: String): Option[Cluster]

  def provision(cluster: Cluster): F[Either[String, Unit]]

  def start(cluster: Cluster): F[Either[FailedStartingUp, Unit]]

  def stop(cluster: Cluster): F[Either[String, Unit]]

  def tearDown(cluster: Cluster): F[Either[String, Unit]]
}

