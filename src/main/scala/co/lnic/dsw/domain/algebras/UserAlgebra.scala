package co.lnic.dsw.domain.algebras

import cats.data._
import co.lnic.dsw.domain.domain._

trait UserAlgebra[F[_]] {
  def byId(id: UserId): OptionT[F, User]
  def shareApplication(applicationId: ApplicationId, withUserId: UserId): EitherT[F, String, Any]
}
