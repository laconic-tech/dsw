package co.lnic.dsw.domain.interpreters.stores

import java.nio.file.Path

import cats._
import cats.data._
import cats.implicits._
import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain.UserAlreadyExists
import co.lnic.dsw.domain.domain._

import scala.collection.concurrent.TrieMap

class MemoryStoreInterpreter[F[_]: Applicative] extends DataStoreAlgebra[F] {

  val users     = new TrieMap[UserId, User]
  val specs     = new TrieMap[ApplicationSpecId, ApplicationSpec]
  val apps      = new TrieMap[ApplicationId, Application]
  val userApps  = new TrieMap[UserId, List[ApplicationId]]

  override def getUserById(username: UserId): OptionT[F, User] =
    OptionT(users.get(username).pure[F])

  override def createUser(username: String,
                          name: String,
                          email: String): EitherT[F, UserAlreadyExists, User] =
    EitherT {
      {
        if (users.contains(username)) {
          Left(UserAlreadyExists(username))
        } else {
          val user = User(username, name, email, "kube-public")
          users.put(user.id, user)
          Right(user)
        }
      }.pure[F]
    }

  override def getApplicationSpecs(): F[Seq[ApplicationSpec]] =
    specs.values.toSeq.filter(_.status == Active).pure[F]

  override def getApplicationSpecBy(specId: ApplicationSpecId): OptionT[F, ApplicationSpec] =
    OptionT(specs.get(specId).pure[F])

  override def createApplicationSpec(name: String, chart: Path, services: Seq[ExposedService]): EitherT[F, String, ApplicationSpec] =
    EitherT {
      val version = specs.keySet.map(_.version).max + 1
      val spec = ApplicationSpec(ApplicationSpecId(name, version), chart, services, Active)
      specs.put(spec.id, spec)

      Either.right[String, ApplicationSpec](spec).pure[F]
    }

  override def getApplicationsByUserId(userId: UserId): F[Seq[Application]] =
    apps.filterKeys(id => userApps.getOrElseUpdate(userId, List()).contains(id))
        .values.toSeq
        .pure[F]

  override def createApplication(): Unit = ???

  override def deleteApplication(): Unit = ???

  override def shareApplication(application: Application, user: User): Unit = ???


}
