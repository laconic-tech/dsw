package co.lnic.dsw.domain.algebras

import cats.data.EitherT
import co.lnic.dsw.domain.domain.ApplicationId
import co.lnic.dsw.domain.domain.User
import co.lnic.dsw.domain.domain.UserId

trait UserAlgebra[F[_]] {
  def byId(id: UserId): EitherT[F, String, User]
  def shareApplication(applicationId: ApplicationId, withUserId: UserId): EitherT[F, String, Any]
}
