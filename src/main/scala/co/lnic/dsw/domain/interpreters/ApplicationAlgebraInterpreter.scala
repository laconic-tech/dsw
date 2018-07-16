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
                         owner: User): EitherT[F, String, RunningApplication] = {

    applicationSpec.status match {

      case Active =>
        // TODO: check whether the owner's namespace exists, failing if it doesn't

        // install the application in the cluster and return the result
        for {
          _         <- cluster.install(applicationSpec.chart, name, owner.namespace)
          app       <- dataStore.createApplication(name, owner.namespace, applicationSpec.id)
          services  <- EitherT.liftF(applicationSpec
                        .services.map(s =>
                          cluster.getServiceUrl(app.name, s.name, app.namespace, s.port)
                               .map(url => Service(s.name, url)))
                               .toList
                               .traverse(x => x))

        } yield RunningApplication(app, services)

      case Draft => EitherT.leftT[F, RunningApplication]("Can't create Application in Draft state.")
      case Disabled(message) => EitherT.leftT[F, RunningApplication](s"Application has been disabled: $message")
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

  /**
    * Gets an application
    *
    * @param id
    * @param userId
    * @return
    */
  override def byIdAndUserId(id: ApplicationId, userId: UserId): OptionT[F, RunningApplication] = {
    for {
      app       <- dataStore.getApplicationBy(id, userId)
      spec      <- dataStore.getApplicationSpecBy(app.applicationSpecId)
      services  <- OptionT.liftF(
                      spec.services.map(s =>
                        cluster.getServiceUrl(app.name, s.name, app.namespace, s.port).map(url => Service(s.name, url))
                      )
                      .toList
                      .traverse(x => x)
                   )
    } yield RunningApplication(app, services)
  }
}
