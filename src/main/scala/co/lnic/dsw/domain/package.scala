package co.lnic.dsw.domain

import java.util.UUID

package object domain {

  // users and roles
  type UserId = String
  case class User(id: UserId, name: String, email: String)

  // cluster definitions
  case class ClusterSpecification(name: String,
                                  chart: String, // URI to tiller repo?
                                  services: Seq[ExposedService],
                                  status: ClusterSpecificationStatus)

  // an specification can expose services
  case class ExposedService(name: String, description: String, iconUri: Option[String], protocol: ServiceProtocol, port: Int)

  // supported service protocols
  sealed trait ServiceProtocol
  case object Http extends ServiceProtocol
  case object SSH extends ServiceProtocol
  // case object KernelProxy extends ServiceProtocol // example of other backends


  // supported cluster spec status
  sealed trait ClusterSpecificationStatus
  case object Active extends ClusterSpecificationStatus
  case object Disabled extends ClusterSpecificationStatus

  // resources
  case class StorageResource(name: String, owner: UserId, visibility: Visibility, uri: String)

  // whether a resource is available to every user or just to the owner
  sealed trait Visibility
  case object Public extends Visibility
  case object Private extends Visibility

  // cluster instances
  case class Cluster(id: UUID, name: String, namespace: String, )
}
