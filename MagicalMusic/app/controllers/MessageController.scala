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

    //------------------------------------Settings------------------------------------
    var stringWeight = 1;
    var punctWeight = 1;
    var emojiWeight = 1;
    var emoticonWeight = 1;

    var stringMinAmount = 2;
    var punctMinAmount = 1;
    var emojiMinAmount = 1;
    var emoticonMinAmount = 1;


    //----------------------------------Initialization--------------------------------
    var stringList = List[String]();
    var punctList = List[String]();
    var emojiList = List[String]();
    var emoticonList = List[String]();
    var stringTemp = "";

    var containsString = 0;
    var containsPunct = 0;
    var containsEmoji = 0;
    var containsEmoticon = 0;

    var stringVA = Vector(0.0, 0.0);
    var punctVA = Vector(0.0, 0.0);
    var emojiVA = Vector(0.0, 0.0);
    var emoticonVA = Vector(0.0, 0.0); 

    var messageVA = Vector(0.0, 0.0);


    //------------------------------------Tokenizer-----------------------------------
    //Specifically written for small text with emoticons and puntuation errors.
    var tokens = message.split(" ");

    for (token <- tokens){
      stringTemp = "";
      var emoticon = false;
      var emoji = false;

      for (charTemp <- token){
        //Type is string
        if (((charTemp >= 'A' && charTemp <= 'Z') || (charTemp >= 'a' && charTemp <= 'z')) && !emoticon && !emoji){
          stringTemp += charTemp;
        }
        //Type is puntuation
        else if((charTemp == '.' || charTemp == ',' || charTemp == '!' || charTemp == '?') && !emoticon && !emoji){
          if (stringTemp != ""){
            stringList = (stringTemp :: stringList);
            stringTemp = "";
          }
          stringTemp += charTemp;
          punctList = (stringTemp :: punctList);
          stringTemp = "";
        }
        //Type is emoji
        else if((charTemp == '%' || emoji) && !emoticon){
          stringTemp += charTemp;
          emoji = true;
        }
        //Type is textual emoticon
        else 
        {
          if (stringTemp != "" && !emoticon){
            stringList = (stringTemp :: stringList);
            stringTemp = "";
          }
          emoticon = true;
          stringTemp += charTemp;
        }
      } 
      //Reset lists   
      if (stringTemp != ""){
        if (emoji) {
          emojiList = (stringTemp :: emojiList);
        }
        else if (emoticon){
          emoticonList = (stringTemp :: emoticonList);
        }
        else {
          stringList = (stringTemp :: stringList);
        }
      }
    }

    //-------------------------------Dictionary Lookup---------------------------------

    var count = 0;
    for (string <- stringList){
      //Query tabel sentimentdictionary with string


      count += 1;
      if (count >= stringMinAmount){
        containsString = 1;
      }
      Logger.debug(string);
    }

    count = 0;
    for (punct <- punctList){
      //Query tabel punctuationdictionary with punct


      count += 1;
      if (count >= punctMinAmount){
        containsPunct = 1;
      }
      Logger.debug(punct);
    }

    count = 0;
    for (emoji <- emojiList){
      //Query tabel emojidictionary with emoji



      count += 1;
      if (count >= emojiMinAmount){
        containsEmoji = 1;
      }
      Logger.debug(emoji);
    }


    count = 0;
    for (emoticon <- emoticonList){
      //Query tabel emoticondictionary with emoticon

      count += 1;
      if (count >= emoticonMinAmount){
        containsEmoticon = 1;
      }
      Logger.debug(emoticon);
    }

    //---------------------------Calculate Message Vector-------------------------------
    //Scale vectors with settings
    var stringScaler = containsString * (stringWeight / (punctWeight * emojiWeight * emoticonWeight));
    var punctScaler = containsPunct * (punctWeight / (stringWeight * emojiWeight * emoticonWeight));
    var emojiScaler = containsEmoji * (emojiWeight / (punctWeight * stringWeight * emoticonWeight));
    var emoticonScaler = containsEmoticon * (emoticonWeight / (punctWeight * emojiWeight * stringWeight));

    // stringVA *= stringScaler;
    // punctVA *= punctScaler;
    // emojiVA *= emojiScaler;
    // emoticonVA *= emoticonScaler;

    //Get result vector
    //messageVA = stringVA * punctVA * emojiVA * emoticonVA

    var query = message;
    var youtube = new helpers.Search();
    var id = youtube.getVideoIDFromQuery(message);
    Ok(Json.toJson((Message(id))))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

}