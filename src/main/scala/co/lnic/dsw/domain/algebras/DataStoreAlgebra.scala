package co.lnic.dsw.domain.algebras

import cats.data._
import cats.implicits._

import co.lnic.dsw.domain.domain._

trait DataStoreAlgebra[F[_]] {

  // user related
  def getUserById(userId: UserId): OptionT[F, User] = ???
  def createUser()

  // application specs
  def getApplicationSpecs()
  def getApplicationSpecBy(specId: ApplicationSpecId): OptionT[F, ApplicationSpec] = ???
  def createApplicationSpec()

  // application
  def getApplicationsByUserId(): F[Seq[Application]]
  def createApplication()
  def deleteApplication()
  def shareApplication(application: Application, user: User)
}
