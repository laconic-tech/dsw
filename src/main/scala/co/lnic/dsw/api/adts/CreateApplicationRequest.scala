package co.lnic.dsw.api.adts

import co.lnic.dsw.domain.domain.ApplicationSpecId

case class CreateApplicationRequest(name: String, specId: ApplicationSpecId)
