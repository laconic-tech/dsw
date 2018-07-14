package co.lnic.dsw.stories

class UserSpec extends Specification {

  info("As a `Data Analyst`")
  info("I want to ...")
  info("So that I can ...")

  feature("Login") _
  feature("Logout") _
  feature("Request and provision storage resources") _
  feature("Request Access to other user's provisioned resources") _
  feature("Share one of my resources with another user") _
  feature("Share one of my resources with an external user") _

  feature("Be able to set rules on when a cluster has to be shutdown") {
    scenario("Shutdown a cluster after a number of hours") _
    scenario("Shutdown a cluster after a period of inactivity") _
    scenario("Shutdown a cluster after spending threshold") _
  }

}


class StorageResourcesSpec extends Specification {

  info("As a `Data Analyst`")
  info("I would like to be able to keep my data stored securely")
  info("and available from every cluster I provision")
  info("so that my data is not lost after a cluster is stopped")
}

class ApplicationSpec extends Specification {

  info("As a `Data Analyst`")
  info("I want to be able to provision a cluster on-demand")
  info("so that I perform tasks as data analysis")

  feature("Listing available Cluster specifications") {
    // simple list of all clusters that this user can create
  }

  feature("Provision a cluster from a specification") {
    // we should attach all of the users storage options
    // additionally allow to attach other resources on all of the containers
    // while it is being deployed, show the logs/events
  }

  feature("Scale up / down a running cluster") _
  feature("Stop a previously provisioned cluster") _
  feature("Start an stopped cluster") _
  feature("Restart a cluster") _
  feature("Destroy a previously provisioned cluster") _
  feature("Share a cluster instance with another Data Analyst") _
}

class ApplicationSpecSpec extends Specification {
  info("As an `Administrator`")
  info("I want to be able to define cluster specifications")
  info("and allow users to create instances from them")

  feature("Create a definition based on a helm chart") {
    scenario("Create a definition based on an external helm chart") _
    scenario("Create a definition based on an existing cluster definition") _
    scenario("Create a definition where certain values can be overridden at provisioning time") _
    scenario("Create a definition that exposes an SSH session and an Http service") {
      // so that when a user starts an instance up, we can proxy and expose them in the UI/server
    }

    scenario("Define ranges on valid number of instances") _
  }

  feature("Enable/Disable creation of instances on a cluster") _
}

class UserAssignedBudgetReport extends Specification {
  // be able to define a budget for an analyst or a group of them
  // and track progress against it
}

class UserSpendingReportingSpec extends Specification {
  info("As a `Data Analyst`")
  info("I want to know an estimate of how much I've spent on a given instance")

  feature("Spending estimate on a running cluster") _
  feature("Spending on a User") _
}

class DashboardSpec extends Specification {
  scenario("Show number of resources available/used") _
  scenario("Show health indicators") _
}