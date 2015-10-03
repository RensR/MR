package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import play.api.Logger
import java.io.IOException

import helpers.Youtube._

case class Message(value: String)

class MessageController extends Controller {

  implicit val fooWrites = Json.writes[Message]

//The function to get the song.
//message = the text the user has typed into the textbox.
  def getSong(message: String) = Action{
    //get VA from message
    //Match VA with song in database
    //return the result (hardcoded youtube song right now)

    var youtube = new helpers.Youtube();
    var id = ""
    try{
        id = youtube.getVideoIdFromQuery(message);
    }
    catch{ 
        case _: Throwable => Logger.debug("Error caught in getVideoIdFromQuery")
    }
    finally{
        Ok(Json.toJson((Message(id))))
    }
    Ok(Json.toJson((Message(id))))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

}