package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import play.api.Logger
import java.io.IOException

import helpers.Search._

case class Message(value: String)

class MessageController extends Controller {

  implicit val fooWrites = Json.writes[Message]
  
//The function to get the song.
//message = the text the user has typed into the textbox.
  def getSong(message: String) = Action{
    //get VA from message
    //Match VA with song in database
    //return the result (hardcoded youtube song right now)
    //the query will be the artist and song name.

    var tokens = message.split(" ");

    var stringList = List[String]();
    var punctuation = List('.', ',', '!', '?');
    var stringTemp = "";

    for (token <- tokens){
      stringTemp = "";
      var first = true;

      for (charTemp <- token){
        if (((charTemp >= 'A' && charTemp <= 'Z') || (charTemp >= 'a' && charTemp <= 'z')) || !first){
          stringTemp += charTemp;
        }
        else if(charTemp == '.' || charTemp == ',' || charTemp == '!' || charTemp == '?'){
          if (stringTemp != ""){
            stringList = (stringTemp :: stringList);
            stringTemp = "";
          }
          stringTemp += charTemp;
          stringList = (stringTemp :: stringList);
          stringTemp = "";
        }
        else if(charTemp == '%'){
          stringTemp += charTemp;
          first = false;
        }
        else
        {
          if (stringTemp != ""){
            stringList = (stringTemp :: stringList);
            stringTemp = "";
          }
          first = false;
          stringTemp += charTemp;
        }
      }    
      if (stringTemp != ""){
        stringList = (stringTemp :: stringList);
      }
    }

    for (s <- stringList){
      Logger.debug(s);
    }


    var query = message;
    var youtube = new helpers.Search();
    var id = youtube.getVideoIDFromQuery(message);
    Ok(Json.toJson((Message(id))))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

}