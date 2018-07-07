package co.lnic.dsw.domain

package object domain {

  // users and roles
  type UserId = String
  case class User(id: UserId, name: String, email: String)

  // cluster definitions
  case class ClusterSpecification(name: String,
                                  services: Seq[ExposedService],
                                  status: ClusterSpecificationStatus)

  // an specification can expose services
  case class ExposedService(name: String, description: String, iconUri: Option[String], protocol: ServiceProtocol, port: Int)

  // supported service protocols
  sealed trait ServiceProtocol
  case object Http extends ServiceProtocol
  case object SSH extends ServiceProtocol

  // supported cluster spec status
  sealed trait ClusterSpecificationStatus
  case object Enabled extends ClusterSpecificationStatus
  case object Disabled extends ClusterSpecificationStatus

  // resources
  case class StorageResource(name: String, owner: UserId, visibility: Visibility, uri: String)

  // whether a resource is available to every user or just to the owner
  sealed trait Visibility
  case object Public extends Visibility
  case object Private extends Visibility

  // cluster instances
}
