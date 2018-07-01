package co.lnic.dsw.api

import cats.effect.Effect
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class WorkbenchService[F[_]: Effect] extends Http4sDsl[F] {

  /** ideas:
    *
    * - for each user create a kubernetes namespace, where the user can deploy clusters
    * - include some kind of monitoring of prices/budget/spending
    * - charts are helm charts, allow to override their settings
    * - have the notion of environments / deployments?
    * - deploy infrastructure too? like buckets
    */

  // other future cool things to do
  // use something like https://github.com/Microsoft/monaco-editor to browse/edit files in the container
  // along a file system or project explorer


  val service: HttpService[F] = {
    HttpService[F] {
      // admin / monitoring endpoints
      case GET -> Root / "console" =>
        // display version information and human readeable feedback
        // on the state of the app:
        // hostname
        // uptime
        // version
        Ok()

      case GET -> Root / "health" =>
        // health check endpoint
        // check connection to kubernetes master:
        Ok()

      case GET -> Root / "charts" =>
        // list preconfigured charts in the system
        // these can be used to create clusters that users can attach to
        Ok()

      case GET -> Root / "clusters" =>


        
        // list all known clusters in the system
        // cluster are instances of a pre-configured definition
        Ok()

      case GET -> Root / "clusters" / name =>
        Ok()

      case GET -> Root / "clusters" / name / "start" =>
        Ok()

      case GET -> Root / "clusters" / name / "stop" =>
        Ok()

      case GET -> Root / "clusters" / name / "delete" =>
        Ok()

      case GET -> Root / "clusters" / name / "service" =>
        // establish forwarding between the server and a given pod
        // and act as a proxy for traffic
        Ok()

      case GET -> Root / "clusters" / name / "console" =>
        // through the kubernetes api
        // ideas could be to use something like: https://github.com/nickola/web-console

        Ok()
    }
  }
}
