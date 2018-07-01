package co.lnic.dsw.api

import co.lnic.dsw.domain.clusters.ClusterService

import cats.effect.Effect
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl

class UserEndpoints[F[_]: Effect] extends Http4sDsl[F] {
  
  def create(clusterService: ClusterService[F]): HttpService[F] =
    HttpService[F] {

      case GET -> Root / "users" / name =>
        Ok()

      case GET -> Root / "users" / name / "workbench" =>
        // return the user's workbench
        // consisting of a list of files and a location
        Ok()

      case GET -> Root/ "users" / name / "clusters" =>
        Ok()

      case POST -> Root / "users" / name / "clusters" =>
        // create a new cluster for this user
        Ok()
    }
}
