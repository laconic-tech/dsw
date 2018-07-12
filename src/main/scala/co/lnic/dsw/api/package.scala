package co.lnic.dsw

import java.util.UUID

import co.lnic.dsw.domain.algebras.DataStoreAlgebra
import co.lnic.dsw.domain.domain._
import org.http4s._
import org.http4s.server._
import org.http4s.server.middleware.authentication.BasicAuth
import org.http4s.server.middleware.authentication.BasicAuth.BasicAuthenticator

import scala.util.Try

package object api {

//  class Authentication[F[_]](store: DataStoreAlgebra[F]) {
//    val authStore: BasicAuthenticator[F, User] = (creds: BasicCredentials) => store.getUserById(creds.username).value
//    val auth: AuthMiddleware[F, User] = BasicAuth("dsw", authStore)
//  }

  object ApplicationIdVar {
    def unapply(str: String): Option[UUID] = Try(UUID.fromString(str)).toOption
  }
}
