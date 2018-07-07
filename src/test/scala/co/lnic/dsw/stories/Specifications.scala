package co.lnic.dsw.stories

class UserSpec extends Specification {

  info("As a `Data Analyst`")
  info("I want to ...")
  info("So that I can ...")

  feature("Login")
  feature("Logout")
  feature("Request and provision storage resources")
  feature("Request Access to other user's provisioned resources")
  feature("Share one of my resources with another user")
  feature("Share one of my resources with an external user")
  feature("Request Access to the `Shared Area`")
}


class StorageResourcesSpec extends Specification {

  info("As a `Data Analyst`")
  info("I would like to be able to keep my data stored securely")
  info("and available from every cluster I provision")
  info("so that my data is not lost after a cluster is stopped")
  info("and also so that ")
}

class ClusterProvisioningSpec extends Specification {

  info("As a `Data Analyst`")
  info("I want to be able to provision a cluster on-demand")
  info("so that I perform tasks as data analysis")

  feature("Listing available Cluster specifications")
  feature("Provision a cluster from a specification")
  feature("Stop a previously provisioned cluster")
  feature("Start an stopped cluster")
  feature("Restart a cluster")
  feature("Destroy a previously provisioned cluster")
  feature("Share a cluster instance with another Data Analyst")
}

class ClusterDefinitionSpec extends Specification {
  info("As an `Administrator`")
  info("I want to be able to define cluster specifications")
  info("and allow users to create instances from them")

  feature("Create a definition based on a helm chart") {
    scenario("Create a definition based on an external helm chart")
    scenario("Create a definition based on an existing cluster definition")
    scenario("Create a definition where certain values can be overridden at provisioning time")
  }

  feature("Enable/Disable creation of instances on a cluster")
}
