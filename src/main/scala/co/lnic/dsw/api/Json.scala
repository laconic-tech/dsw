//package co.lnic.dsw.api
//
//import co.lnic.dsw.domain.domain._
//import io.circe._, io.circe.generic.semiauto._
//
//object Json {
//
//  object Implicits {
//    implicit val applicationDecoder: Decoder[Application] = deriveDecoder
//    implicit val applicationEncoder: Encoder[Application] = deriveEncoder
//
//    implicit val applicationStateDecoder: Decoder[ApplicationState] = deriveDecoder
//    implicit val applicationStateEncoder: Encoder[ApplicationState] = deriveEncoder
//
//    implicit val applicationWithStateDecoder: Decoder[Application with ApplicationState] = deriveDecoder
//    implicit val applicationWithStateEncoder: Encoder[Application with ApplicationState] = deriveEncoder
//  }
//}
