package co.lnic.dsw.domain.interpreters

import cats.Applicative
import cats.data._
import cats.implicits._
import co.lnic.dsw.domain.algebras.UserAlgebra
import co.lnic.dsw.domain.domain._

import scala.language.higherKinds

class UserAlgebraInterpreter[F[_]: Applicative] extends UserAlgebra[F] {

  override def byId(id: UserId): EitherT[F, String, User] =
    EitherT.rightT[F, String](User(id, "test", "test@test.com", "kube-default"))

  override def shareApplication(applicationId: ApplicationId, withUserId: UserId): EitherT[F, String, Any] = ???
}
