package co.lnic.dsw.domain.interpreters

import java.util.UUID

import cats._
import cats.data._
import cats.implicits._

import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain._

import scala.language.higherKinds


class ApplicationAlgebraInterpreter[F[_]: Monad](cluster: ClusterAlgebra[F])
  extends ApplicationAlgebra[F] {

  /**
    * Provisions an application into the cluster
    *
    * @param name
    * @param applicationSpec
    * @param owner
    * @return
    */
  override def provision(name: String,
                         applicationSpec: ApplicationSpec,
                         owner: User): EitherT[F, String, Application] = {

    applicationSpec.status match {
      case Active =>
        // TODO: check whether the owner's namespace exists, failing if it doesn't

        // install the application in the cluster and return the result
        cluster.install(applicationSpec.chart, name, owner.namespace)
               .map { _ =>
                  Application(
                    UUID.randomUUID,
                    name,
                    owner.namespace,
                    applicationSpec.id
                  )
               }

      case Disabled => EitherT.leftT[F, Application]("Application has been disabled")
    }
  }

  /**
    * Removes an application from the cluster
    *
    * @param application
    * @return
    */
  override def stop(application: Application): EitherT[F, String, Application] = {
    // delete the application in the cluster
    cluster.kill(application.name, application.namespace)
      .map(_ => application) // TODO: What to do
  }

  /**
    * Returns the state of an application in the cluster
    *
    * @param application
    * @return
    */
  override def status(application: Application): EitherT[F, String, ApplicationState] = ???

  /**
    * Returns all non-deleted applications for a given user
    * irrespective if they are running or not.
    *
    * @param user
    * @return
    */
  override def byUser(user: User): F[List[Application]] = {
    // TODO: check the state of the application in the cluster or some sort of cached view
    List.empty[Application].pure[F]
  }
}