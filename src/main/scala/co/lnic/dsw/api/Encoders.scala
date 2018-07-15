package co.lnic.dsw.api

import java.nio.file.Path

import io.circe._

object Encoders {

  implicit val pathEncoder: Encoder[Path] = (a: Path) => Json.fromString(a.toString)
}
