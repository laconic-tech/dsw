package co.lnic.dsw.domain.interpreters

import cats._
import cats.data._
import cats.implicits._

import co.lnic.dsw.domain.domain._
import co.lnic.dsw.domain.algebras._


import scala.language.higherKinds

class UserAlgebraInterpreter[F[_]: Applicative](dataStore: DataStoreAlgebra[F]) extends UserAlgebra[F] {

  override def byId(id: UserId): OptionT[F, User] = dataStore.getUserById(id)
  override def shareApplication(applicationId: ApplicationId, withUserId: UserId): EitherT[F, String, Any] = ???
}
