package co.lnic.dsw.domain.algebras

import cats.data._
import co.lnic.dsw.domain.domain.Application
import co.lnic.dsw.domain.domain.ApplicationId
import co.lnic.dsw.domain.domain.ApplicationSpec
import co.lnic.dsw.domain.domain.ApplicationState
import co.lnic.dsw.domain.domain.User

trait ApplicationAlgebra[F[_]] {
  /**
    * Provisions an application into the cluster
    * @param name
    * @param cluster
    * @param owner
    * @return
    */
  def provision(name: String, cluster: ApplicationSpec, owner: User): EitherT[F, String, Application]

  /**
    * Removes an application from the cluster
    * @param application
    * @return
    */
  def stop(application: Application): EitherT[F, String, Application]

  /**
    * Returns the state of an application in the cluster
    * @param application
    * @return
    */
  def status(application: Application): EitherT[F, String, ApplicationState]

  /**
    * Returns all non-deleted applications for a given user
    * irrespective if they are running or not.
    * @param user
    * @return
    */
  def byUser(user: User): F[List[Application]]

  /**
    * Returns an specific app
    * @param appId
    * @return
    */
  def byId(appId: ApplicationId): OptionT[F, Application]
}

