package co.lnic.dsw.domain.interpreters.stores

import java.nio.file.Path
import java.util.UUID

import cats._
import cats.data._
import cats.implicits._
import co.lnic.dsw.domain.algebras._
import co.lnic.dsw.domain.domain.UserAlreadyExists
import co.lnic.dsw.domain.domain._

import scala.collection.concurrent.TrieMap
import scala.util.Try

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
      val version = Try(specs.keySet.map(_.version).max + 1).getOrElse(1)
      val spec = ApplicationSpec(ApplicationSpecId(name, version), chart, services, Active)
      specs.put(spec.id, spec)

      Either.right[String, ApplicationSpec](spec).pure[F]
    }

  override def getApplicationsByUserId(userId: UserId): F[Seq[Application]] =
    apps.filterKeys(id => userApps.getOrElseUpdate(userId, List()).contains(id))
        .values.toSeq
        .pure[F]


  override def deleteApplication(): Unit = ???

  override def shareApplication(application: Application, user: User): Unit = ???

  override def createApplication(name: String, namespace: String, specId: ApplicationSpecId, userId: UserId): EitherT[F, String, Application] = {
    EitherT {
      val app = Application(UUID.randomUUID, name, namespace, specId)
      apps.put(app.id, app)
      userApps.get(userId) match {
        case Some(items) => userApps.put(userId, app.id :: items)
        case None => userApps.put(userId, List(app.id))
      }
      Either.right[String, Application](app).pure[F]
    }
  }

  override def getApplicationBy(id: ApplicationId, userId: UserId): OptionT[F, Application] =
    OptionT {
      getApplicationsByUserId(userId).map(_.find(app => app.id == id))
    }

  override def getApplicationBy(id: ApplicationId): OptionT[F, Application] =
    OptionT {
      apps.filterKeys(key => key == id).values.headOption.pure[F]
    }
}
