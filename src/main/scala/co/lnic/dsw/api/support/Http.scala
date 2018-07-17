package co.lnic.dsw.api.support

import cats.effect.Effect
import cats.implicits._
import org.http4s._
import org.http4s.client.blaze._

class Http[F[_]: Effect] {
  /**
    * Proxies a request to a different endpoint
    * @param req
    * @param to
    * @return
    */
  def proxy(req: Request[F], to: Uri): F[Response[F]] = {
    for {
      client <- Http1Client[F]()
      // create the path for the request
      request = req.withUri(to)
      // execute the request, and get the response back
      response <- client.fetch(request)(_.pure[F])
    } yield response
  }
}
