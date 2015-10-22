package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Routes
import play.api.Logger
import play.api._
import play.api.db._
import play.api.Play.current
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
    Logger.debug("Query = " + query.toString())

    var id = "No results."
    var i = 0;
    var songIDs = getSongID(query.v, query.a)
    while (id == "No results." && i < songIDs.length){
      Logger.debug("Song found!  " + songIDs(i).toString())
      var youtube = new helpers.Search()
      id = youtube.getVideoIDFromQuery(songIDs(i).toString())
      Logger.debug(songIDs(i).totalString())
      i += 1
    }
    Ok(Json.toJson((Message(id))))
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.MessageController.getSong)).as(JAVASCRIPT)
  }

  def getSongID(vvalence : Double, varousal : Double): List[Song] = {
    var valence = 0.2
    var arousal = 0.2
    var tempValence = 0.0
    var tempArousal = 0.0

    //Used for scaling the values
    var audioWeight = 5
    var lyricsWeight = 3
    var containsLyrics = false

    if (!vvalence.isNaN) {
        valence = vvalence
    }
    if (!arousal.isNaN) {
        arousal = varousal
    }

    var songs = List[Song]()
    DB.withConnection{ conn =>
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("""SELECT song, artist, valenceAudio, arousalAudio, valenceLyrics, arousalLyrics FROM 
            ( ( SELECT song, artist, valenceAudio, arousalAudio, valenceLyrics, arousalLyrics, """ + valence + """-valenceAudio AS diff
                FROM musiclyrics
                WHERE valenceAudio < """ + valence + """
                ORDER BY valenceAudio DESC
                  LIMIT 200
              ) 
              UNION ALL
              ( SELECT song, artist, valenceAudio, arousalAudio, valenceLyrics, arousalLyrics, valenceAudio-""" + valence + """ AS diff
                FROM musiclyrics
                WHERE valenceAudio >= """ + valence + """
                ORDER BY valenceAudio ASC
                  LIMIT 200
              ) 
            ) AS tmp
            ORDER BY diff
            LIMIT 200 ;""".replaceAll("\n", " "))



        while(rs.next()){
            var song = rs.getString("song")
            var artist = rs.getString("artist")
            var tempArousalAudio = rs.getDouble("arousalAudio")
            var tempValenceAudio = rs.getDouble("valenceAudio")
            var tempArousalLyrics = rs.getDouble("arousalLyrics")
            var tempValenceLyrics = rs.getDouble("valenceLyrics")

            if (tempValenceLyrics > 0 || tempArousalLyrics > 0)
              containsLyrics = true

            //Convert with weights
            if (containsLyrics){
              tempValenceLyrics = (tempValenceLyrics - 1.9) * 5.3
              tempArousalLyrics = (tempArousalLyrics - 1.3) * 2.0

              tempValence = (audioWeight * tempValenceAudio + lyricsWeight * tempValenceLyrics) / 2
              tempArousal = (audioWeight * tempArousalAudio + lyricsWeight * tempArousalLyrics) / 2
            }
            else {
              tempValence = tempValenceAudio
              tempArousal = tempArousalAudio
            }

            containsLyrics = false

            // Logger.debug("song = " + song + " from " + artist + "   with valence and arousal " + tempValence + ", " + tempArousal
            //     + " with distance " + math.abs(arousal - tempArousal) + math.abs(valence - tempValence))


            songs = new Song(artist, song, tempValence, tempArousal) :: songs
        }
    }
    // Sorts the songs by distance and return the best one
    songs.sortWith(_.distance(valence, arousal) < _.distance(valence, arousal))
  }


  def computeTokens(message : String): VAVector = {
    //------------------------------------Settings------------------------------------
    var stringWeight = 5.0
    var punctWeight = 1.0
    var emojiWeight = 5.0
    var emoticonWeight = 3.0

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

    var containsString = false
    var containsPunct = false
    var containsEmoji = false
    var containsEmoticon = false

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

    var count = 0
    var aStdSum = 0.0
    var vStdSum = 0.0

    for (string <- stringList){
      Logger.debug("String = " + string.toLowerCase())
      //Query tabel sentimentdictionary with string
      DB.withConnection{ conn =>
      val stmt = conn.createStatement
        val rs = stmt.executeQuery("SELECT * FROM textdictionary WHERE Word = '" + string.toLowerCase() + "'")
        // Test if it's a ANEW word...
        while(rs.next()){
          count += 1
          var vMean = rs.getDouble("VMean")
          var vStd = rs.getDouble("VSTD")
          var aMean = rs.getDouble("AMean")
          var aStd = rs.getDouble("ASTD")
          vStdSum += (1 / vStd)
          aStdSum += (1 / aStd)

          //temporary
          var v = vMean / vStd
            var a = aMean / aStd
            var VA = new VAVector(v, a)

            //add the vector to the list of vectors
            stringVAs = VA :: stringVAs
        }
      }
    }
    if (count >= stringMinAmount){
      for(e <- stringVAs){
        e.v = e.v / vStdSum
        e.a = e.a / aStdSum
      }
      stringVA.Sum(stringVAs)

      Logger.debug("StringVA: " + stringVA.toString() + "\n")
      containsString = true
    }

    var vMean = 0.0
    var vStd = 0.0
    var aMean = 0.0
    var aStd = 0.0

    aStdSum = 0.0
    vStdSum = 0.0

    count = 0
    for (punct <- punctList){
      //Query tabel punctuationdictionary with punct
      
      if (punct == "!" || punct == "?"){
        if (punct == "!"){
          vMean = 0.64583
          vStd = 0.93992 
          aMean = 0.84659
          aStd = 0.81486
        }
        else if (punct == "?"){
          vMean = -0.1458
          vStd = 0.54628
          aMean = -0.7457
          aStd = 0.69793
        }
        count += 1
        vStdSum += (1 / vStd)
        aStdSum += (1 / aStd)

        var v = vMean / vStd
        var a = aMean / aStd
        var VA = new VAVector(v, a)

        //add the vector to the list of vectors
        punctVAs = VA :: punctVAs
        
      }
    }
    if (count >= punctMinAmount){
      for(e <- punctVAs){
        e.v = e.v / vStdSum
        e.a = e.a / aStdSum
      }
      punctVA.Sum(punctVAs)

      Logger.debug("punctVA: " + punctVA.toString() + "\n")
      containsPunct = true
    }

    count = 0 
    aStdSum = 0.0
    vStdSum = 0.0

    for (emoji <- emojiList){
      //Query tabel emojidictionary with emoji
      DB.withConnection{ conn =>
      val stmt = conn.createStatement
        var queryString = "SELECT * FROM emoji WHERE ID = " + emoji.stripPrefix("%")
        Logger.debug("Emoji = " + emoji)
        val rs = stmt.executeQuery(queryString)

        //Test if it's a ANEW word...
         while(rs.next()){
          count += 1
            var vMean = rs.getDouble("VMean")
            var vStd = rs.getDouble("VSTD")
            var aMean = rs.getDouble("AMean")
            var aStd = rs.getDouble("ASTD")
            vStdSum += (1 / vStd)
            aStdSum += (1 / aStd)

            //temporary
            var v = vMean / vStd
            var a = aMean / aStd
            var VA = new VAVector(v, a)

            //add the vector to the list of vectors
            emojiVAs = VA :: emojiVAs
        }
      }
    }
    if (count >= emojiMinAmount){
      for(e <- emojiVAs){
        e.v = e.v / vStdSum
        e.a = e.a / aStdSum
      }
      emojiVA.Sum(emojiVAs)
      
      Logger.debug("EmojiVA: " + emojiVA.toString() + "\n")
      containsEmoji = true
    }

    //---------------------------Calculate Message Vector-------------------------------
    //Scale vectors with settings
    var stringScaler: Double = stringWeight / (punctWeight * emojiWeight * emoticonWeight)
    var punctScaler: Double = punctWeight / (stringWeight * emojiWeight * emoticonWeight)
    var emojiScaler: Double = emojiWeight / (punctWeight * stringWeight * emoticonWeight)
    var emoticonScaler: Double = emoticonWeight / (punctWeight * emojiWeight * stringWeight)

    if(!containsString){ stringScaler = 0}
    if(!containsPunct){ punctScaler = 0}
    if(!containsEmoji){ emojiScaler = 0}
    if(!containsEmoticon){ emoticonScaler = 0}

    var totalScaler = stringScaler + punctScaler + emojiScaler + emoticonScaler

    emojiVA.v = (emojiVA.v + 2.0) / 4.0
    emojiVA.a = (emojiVA.a + 1.6) / 2.9

    stringVA.v = (stringVA.v - 1.2) / 7.3
    stringVA.a = (stringVA.a - 1.6) / 6.2

    punctVA.v = (punctVA.v + 2.0) / 4.0
    punctVA.a = (punctVA.a + 2.0) / 4.0

    stringVA.Multiply(stringScaler / totalScaler)
    punctVA.Multiply(punctScaler / totalScaler)
    emojiVA.Multiply(emojiScaler / totalScaler)
    emoticonVA.Multiply(emoticonScaler / totalScaler)

    var messageVAs = List[VAVector]()

    if(containsString){ messageVAs = stringVA :: messageVAs}
    if(containsPunct){ messageVAs = punctVA :: messageVAs}
    if(containsEmoji){ messageVAs = emojiVA :: messageVAs}
    if(containsEmoticon){ messageVAs = emoticonVA :: messageVAs}

    //Get result vector
    messageVA.Sum(messageVAs)
    Logger.debug("ResultingVA: " + messageVA.toString() + "\n")
    messageVA
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

  def Sum(vectors: List[VAVector]){
    var vSum = 0.0
    var aSum = 0.0

    for (vector <- vectors){
      vSum += vector.v
      aSum += vector.a
    }
    v = vSum
    a = aSum
  }

//calculate the Minkowski distance
  def Distance(vector: VAVector)
  {

  }

  override def toString(): String = "Valence: " + v + ", Arousal: " + a;
}

class Song(artist : String, song: String, valence: Double, arousal: Double){
    def distance(v : Double, a: Double): Double =  math.abs(arousal - a) + math.abs(valence - v)
    override def toString(): String = artist + " " + song
    def totalString(): String = artist + " " + song + " " + valence + " " + arousal
}