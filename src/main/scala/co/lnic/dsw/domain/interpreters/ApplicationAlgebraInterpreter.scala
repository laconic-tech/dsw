package co.lnic.dsw.domain.interpreters

import java.util.UUID

import cats._
import cats.data._
import cats.implicits._

import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain._

import scala.language.higherKinds


class ApplicationAlgebraInterpreter[F[_]: Monad](dataStore: DataStoreAlgebra[F], cluster: ClusterAlgebra[F])
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

      case Draft => EitherT.leftT[F, Application]("Can't create Application in Draft state.")
      case Disabled(message) => EitherT.leftT[F, Application](s"Application has been disabled: $message")
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
    cluster.uninstall(application.name, application.namespace)
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
  // TODO: check the state of the application in the cluster or some sort of cached view
  override def byUser(user: User): F[Seq[Application]] = dataStore.getApplicationsByUserId(user.id)

  /**
    * Returns an specific app
    *
    * @param appId
    * @return
    */
  override def byId(appId: ApplicationId): OptionT[F, Application] = ???
}
