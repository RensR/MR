package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes

case class Message(value: String)

class MessageController extends Controller {

  implicit val fooWrites = Json.writes[Message]
/*
  def getMessage = Action {
    Ok(Json.toJson(Message("LgXH44UE5Ho")))
  }
*/

//The function to get the song.
//message = the text the user has typed into the textbox.
  def getSong(message: String) = Action{

  	//return the result
  	Ok(Json.toJson(message))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

}