package co.lnic.dsw.domain.clusters

case class Chart(id: String, description: String)

case class Cluster(name: String,
                   definition: Chart,
                   overrides: Map[String, String],
                   namespace: String)

// state
trait ClusterState { val name: String }
case object Provisioning extends ClusterState { val name = "Provisioning" }
case object Running extends ClusterState { val name = "Running" }
case object Stopped extends ClusterState { val name = "Stopped" }

// errors
case object ClusterDoesNotExist
case class FailedStartingUp(message: String)