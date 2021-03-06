package co.lnic.dsw.domain.algebras

import java.nio.file.Path

import cats.data._
import cats.implicits._
import co.lnic.dsw.domain.domain._

trait DataStoreAlgebra[F[_]] {

  // user related
  def getUserById(userId: UserId): OptionT[F, User]
  def createUser(username: String, name: String, email: String): EitherT[F, UserAlreadyExists, User]

  // application specs
  def getApplicationSpecs(): F[Seq[ApplicationSpec]]
  def getApplicationSpecBy(specId: ApplicationSpecId): OptionT[F, ApplicationSpec]
  def createApplicationSpec(name: String, chart: Path, services: Seq[ExposedService]): EitherT[F, String, ApplicationSpec]

  // application
  def getApplicationsByUserId(userId: UserId): F[Seq[Application]]
  def getApplicationBy(id: ApplicationId): OptionT[F, Application]
  def getApplicationBy(id: ApplicationId, userId: UserId): OptionT[F, Application]

  def createApplication(name: String, namespace: String, specId: ApplicationSpecId, userId: UserId): EitherT[F, String, Application]
  def deleteApplication()
  def shareApplication(application: Application, user: User)
}
