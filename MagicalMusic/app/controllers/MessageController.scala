package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import play.api.Logger
import play.api._
import play.api.db._
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
    var query = computeTokens(message)
    var youtube = new helpers.Search()
    var id = youtube.getVideoIDFromQuery(message)
    Ok(Json.toJson((Message(id))))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

  def computeTokens(message : String){
    //------------------------------------Settings------------------------------------
    var stringWeight = 1
    var punctWeight = 1
    var emojiWeight = 1
    var emoticonWeight = 1

    var stringMinAmount = 2
    var punctMinAmount = 1
    var emojiMinAmount = 1
    var emoticonMinAmount = 1
    //----------------------------------Initialization--------------------------------
    var stringList = List[String]()
    var punctList = List[String]()
    var emojiList = List[String]()
    var emoticonList = List[String]()
    var stringTemp = ""

    var containsString = 0
    var containsPunct = 0
    var containsEmoji = 0
    var containsEmoticon = 0

    var stringVA = new VAVector(0.0, 0.0)
    var punctVA = new VAVector(0.0, 0.0)
    var emojiVA = new VAVector(0.0, 0.0)
    var emoticonVA = new VAVector(0.0, 0.0) 

    var stringVAs = List[VAVector]()
    var punctVAs = List[VAVector]()
    var emojiVAs = List[VAVector]()
    var emoticonVAs = List[VAVector]() 

    var messageVA = new VAVector(0.0, 0.0)

    //------------------------------------Tokenizer-----------------------------------
    //Specifically written for small text with emoticons and puntuation errors.
    var tokens = message.split("\\s+")

    for (token <- tokens){
      stringTemp = ""
      var emoticon = false
      var emoji = false

      for (charTemp <- token){
        //Type is string
        if (((charTemp >= 'A' && charTemp <= 'Z') || (charTemp >= 'a' && charTemp <= 'z')) && !emoticon && !emoji){
          stringTemp += charTemp
        }
        //Type is puntuation
        else if((charTemp == '.' || charTemp == ',' || charTemp == '!' || charTemp == '?') && !emoticon && !emoji){
          if (stringTemp != ""){
            stringList = stringTemp :: stringList
            stringTemp = ""
          }
          stringTemp += charTemp
          punctList = stringTemp :: punctList
          stringTemp = ""
        }
        //Type is emoji
        else if((charTemp == '%' || emoji) && !emoticon){
          stringTemp += charTemp
          emoji = true
        }
        else{
          //Type is textual emoticon
          if (stringTemp != "" && !emoticon){
            stringList = stringTemp :: stringList
            stringTemp = ""
          }
          emoticon = true
          stringTemp += charTemp
        }
      } 
      //Reset lists   
      if (stringTemp != ""){
        if (emoji) {
          emojiList = stringTemp :: emojiList
        }
        else if (emoticon){
          emoticonList = stringTemp :: emoticonList
        }
        else {
          stringList = stringTemp :: stringList
        }
      }
    }

    //-------------------------------Dictionary Lookup---------------------------------

    // var count = 0
    // for (string <- stringList){
    //   //Query tabel sentimentdictionary with string
    //   DB.withConnection{ conn =>
    //   val stmt = conn.createStatement
    //     val rs = stmt.executeQuery("SELECT * FROM textdictionary WHERE (Word = '" + string + "'")

    //     //Test if it's a ANEW word...
    //     val testEmpty = rs;
    //     if (!testEmpty.next()){
    //       count += 1
    //       for (r <- rs) {
    //         var vMean = r.getInt("VMean")
    //         var vStd = r.getInt("VSTD")
    //         var aMean = r.getInt("AMean")
    //         var aStd = r.getInt("ASTD")

    //         //temporary
    //         var v = vMean * (1.0/vStd)
    //         var a = aMean * (1.0/aStd)
    //         var VA = new VAVector(v, a)

    //         //add the vector to the list of vectors
    //         stringVAs = VA :: stringVAs
    //       }
    //     }
    //   }
      
    //   Logger.debug(string)
    // }
    // if (count >= stringMinAmount){
    //   containsString = 1
    // }

    // count = 0
    // for (punct <- punctList){
    //   //Query tabel punctuationdictionary with punct
    //   DB.withConnection{ conn =>
    //   val stmt = conn.createStatement
    //     val rs = stmt.executeQuery("SELECT * FROM punctuation WHERE (Word = '" + punct + "'")

    //     //Test if it's a ANEW word...
    //     val testEmpty = rs;
    //     if (!testEmpty.next()){
    //       count += 1
    //       for (r <- rs) {
    //         var vMean = r.getInt("VMean")
    //         var vStd = r.getInt("VSTD")
    //         var aMean = r.getInt("AMean")
    //         var aStd = r.getInt("ASTD")

    //         //temporary
    //         var v = vMean * (1.0/vStd)
    //         var a = aMean * (1.0/aStd)
    //         var VA = new VAVector(v, a)

    //         //add the vector to the list of vectors
    //         punctVAs = VA :: punctVAs
    //       }
    //     }
    //   }
      
    //   Logger.debug(punct)
    // }
    // if (count >= punctMinAmount){
    //   containsPunct = 1
    // }

    // count = 0
    // for (emoji <- emojiList){
    //   //Query tabel emojidictionary with emoji
    //   DB.withConnection{ conn =>
    //   val stmt = conn.createStatement
    //     val rs = stmt.executeQuery("SELECT * FROM emoji WHERE (Word = '" + emoji + "'")

    //     //Test if it's a ANEW word...
    //     val testEmpty = rs;
    //     if (!testEmpty.next()){
    //       count += 1
    //       for (r <- rs) {
    //         var vMean = r.getInt("VMean")
    //         var vStd = r.getInt("VSTD")
    //         var aMean = r.getInt("AMean")
    //         var aStd = r.getInt("ASTD")

    //         //temporary
    //         var v = vMean * (1.0/vStd)
    //         var a = aMean * (1.0/aStd)
    //         var VA = new VAVector(v, a)

    //         //add the vector to the list of vectors
    //         emojiVAs = VA :: emojiVAs
    //       }
    //     }
    //   }
      
    //   Logger.debug(emoji)
    // }
    // if (count >= emojiMinAmount){
    //   containsEmoji = 1
    // }


    // count = 0
    // for (emoticon <- emoticonList){
    //   //Query tabel emoticondictionary with emoticon
    //   DB.withConnection{ conn =>
    //   val stmt = conn.createStatement
    //     val rs = stmt.executeQuery("SELECT * FROM textualemoticon WHERE (Word = '" + emoticon + "'")

    //     //Test if it's a ANEW word...
    //     val testEmpty = rs;
    //     if (!testEmpty.next()){
    //       count += 1
    //       for (r <- rs) {
    //         var vMean = r.getInt("VMean")
    //         var vStd = r.getInt("VSTD")
    //         var aMean = r.getInt("AMean")
    //         var aStd = r.getInt("ASTD")

    //         //temporary
    //         var v = vMean * (1.0/vStd)
    //         var a = aMean * (1.0/aStd)
    //         var VA = new VAVector(v, a)

    //         //add the vector to the list of vectors
    //         emoticonVAs = VA :: emoticonVAs
    //       }
    //     }
    //   }
      
    //   Logger.debug(emoticon)
    // }
    // if (count >= emoticonMinAmount){
    //   containsEmoticon = 1
    // }

    //---------------------------Calculate Message Vector-------------------------------
    //Scale vectors with settings
    var stringScaler: Double = containsString * (stringWeight / (punctWeight * emojiWeight * emoticonWeight))
    var punctScaler: Double = containsPunct * (punctWeight / (stringWeight * emojiWeight * emoticonWeight))
    var emojiScaler: Double = containsEmoji * (emojiWeight / (punctWeight * stringWeight * emoticonWeight))
    var emoticonScaler: Double = containsEmoticon * (emoticonWeight / (punctWeight * emojiWeight * stringWeight))

    stringVA.Multiply(stringScaler)
    punctVA.Multiply(punctScaler)
    emojiVA.Multiply(emojiScaler)
    emoticonVA.Multiply(emoticonScaler)

    //Get result vector
    messageVA.Average(List(stringVA, punctVA, emojiVA, emoticonVA))
    Logger.debug(messageVA.toString())
  }
}

class VAVector(valence: Double, arousal: Double){
  var v: Double = valence
  var a: Double = arousal 

  def Multiply(scalar: Double){
    v *= scalar
    a *= scalar
  }

  def Add(vector: VAVector){
    v += vector.v
    a += vector.a
  }

  def Average(vectors: List[VAVector]){
    var vSum = 0.0
    var aSum = 0.0
    var count = 0

    for (vector <- vectors){
      if(vector.a != 0 || vector.v != 0){
        count += 1
      }
      vSum += vector.v
      aSum += vector.a
    }
    v = vSum / count
    a = aSum / count
  }

//calculate the Minkowski distance
  def Distance(vector: VAVector)
  {

  }

  override def toString(): String = "Valence: " + v + ", Arousal: " + a;
}