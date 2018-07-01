package co.lnic.dsw.infra.helm


case class DeploymentStatus()
case class

trait HelmServiceAlgebra[F[_]] {

  def install(name: String,
              chart: String,
              namespace: String,
              values: Map[String, String])
             (dryRun: Boolean = false): F[Any] = ???

  def status(name: String): F[Either[String, DeploymentStatus]]
  def delete(name: String): F[Either[String, DeleteSuccess]] = ???
}
