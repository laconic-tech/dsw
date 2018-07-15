package co.lnic.dsw.domain

import java.nio.file.Path
import java.util.UUID

package object domain {

  // users and roles
  type UserId = String
  case class User(id: UserId, name: String, email: String, namespace: String)

  case class UserAlreadyExists(name: String)

  // cluster definitions
  type ApplicationId = UUID
  case class ApplicationSpecId(name: String, version: Int = 1)
  case class ApplicationSpec(id: ApplicationSpecId,
                             chart: Path,
                             services: Seq[ExposedService],
                             status: ApplicationSpecStatus)

  trait ChartLocation
  case class TarChartLocation(path: Path) extends ChartLocation

  // an specification can expose services
  case class ExposedService(name: String, description: String, iconUri: Option[String], protocol: ServiceProtocol, port: Int)

  // supported service protocols
  sealed trait ServiceProtocol
  case object Http extends ServiceProtocol
  case object SSH extends ServiceProtocol
  // case object JupyterKernelProxy extends ServiceProtocol // example of other backends


  // supported cluster spec status
  sealed trait ApplicationSpecStatus
  case object Draft extends ApplicationSpecStatus
  case object Active extends ApplicationSpecStatus
  case class Disabled(reason: String) extends ApplicationSpecStatus

  // resources
  case class StorageResource(name: String, owner: UserId, visibility: Visibility, uri: String)

  // whether a resource is available to every user or just to the owner
  sealed trait Visibility
  case object Public extends Visibility
  case object Private extends Visibility

  // cluster instances
  case class Application(id: ApplicationId, name: String, namespace: String, applicationSpecId: ApplicationSpecId)

  sealed trait ApplicationState
  case object NotPresent extends ApplicationState
  case object Running extends ApplicationState


  sealed trait ClusterStatus extends Product with Serializable
  case object Online extends ClusterStatus
  case object Offline extends ClusterStatus

  type Port = Int

  // running application
  case class Service(name: String, url: String)
  case class RunningApplication(application: Application, services: Seq[Service])
}
