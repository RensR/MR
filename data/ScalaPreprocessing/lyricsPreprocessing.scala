import java.sql.{Connection, DriverManager, ResultSet};
// import play.api.mvc.{Action, Controller}
// import play.api.libs.json.Json
// import play.api.Routes
// import play.api.Logger
// import play.api._
// import play.api.db._
// import play.api.Play.current
// import java.io.IOException


// Change to Your Database Config
val conn_str = "jdbc:mysql://localhost:3306/magicalmusic?user=root&password=root"
val mysql = "mysql" % "mysql-connector-java" % "5.1.12"
// Load the driver
classOf[com.mysql.jdbc.Driver]

// Setup the connection
val conn = DriverManager.getConnection(conn_str)
try {
    // Configure to be Read Only
    val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

    // Execute Query
    val rs = statement.executeQuery("SELECT * FROM textdictionary WHERE Word = 'aardvark' LIMIT 5")

    // Iterate Over ResultSet
    while (rs.next) {
        println(rs.getString("Word"))
    }
}
finally {
    conn.close
}

// val url = "jdbc:mysql://localhost:3306/magicalmusic"
// val driver = "com.mysql.jdbc.Driver"
// val username = "root"
// val password = "root"
// Class.forName(driver)
// var connection:Connection = DriverManager.getConnection(url, username, password)
// val statement = connection.createStatement
// val rs = statement.executeQuery("INSERT INTO musiclyrics (id,msdid,artist,song,valenceAudio,arousalAudio,valenceLyrics,arousalLyrics) VALUES (1,sadasa,daad,sdaasd,1.1,1.2,1.3,1.4);")
   



  //  var tokens = message.split("\\s+")

  //   for (token <- tokens){
  //     stringTemp = ""
  //     var emoticon = false
  //     var emoji = false

  //     for (charTemp <- token){
  //       //Type is string
  //       if (((charTemp >= 'A' && charTemp <= 'Z') || (charTemp >= 'a' && charTemp <= 'z')) && !emoticon && !emoji){
  //         stringTemp += charTemp
  //       }
  //       //Type is puntuation
  //       else if((charTemp == '.' || charTemp == ',' || charTemp == '!' || charTemp == '?') && !emoticon && !emoji){
  //         if (stringTemp != ""){
  //           stringList = stringTemp :: stringList
  //           stringTemp = ""
  //         }
  //         stringTemp += charTemp
  //         punctList = stringTemp :: punctList
  //         stringTemp = ""
  //       }
  //       //Type is emoji
  //       else if((charTemp == '%' || emoji) && !emoticon){
  //         stringTemp += charTemp
  //         emoji = true
  //       }
  //       else{
  //         //Type is textual emoticon
  //         if (stringTemp != "" && !emoticon){
  //           stringList = stringTemp :: stringList
  //           stringTemp = ""
  //         }
  //         emoticon = true
  //         stringTemp += charTemp
  //       }
  //     } 
  //     //Reset lists   
  //     if (stringTemp != ""){
  //       if (emoji) {
  //         emojiList = stringTemp :: emojiList
  //       }
  //       else if (emoticon){
  //         emoticonList = stringTemp :: emoticonList
  //       }
  //       else {
  //         stringList = stringTemp :: stringList
  //       }
  //     }
  //   }

  //   //-------------------------------Dictionary Lookup---------------------------------

  //   var count = 0
  //   for (string <- stringList){
  //     //Query tabel sentimentdictionary with string
  //     DB.withConnection{ conn =>
  //     val stmt = conn.createStatement
  //       val rs = stmt.executeQuery("SELECT * FROM textdictionary WHERE (Word = '" + string + "')")
  //       // Test if it's a ANEW word...
  //       while(rs.next()){
  //         count += 1
  //         var vMean = rs.getDouble("VMean")
  //         var vStd = rs.getDouble("VSTD")
  //         var aMean = rs.getDouble("AMean")
  //         var aStd = rs.getDouble("ASTD")

  //         //temporary
  //         var v = vMean * (1.0/vStd)
  //         var a = aMean * (1.0/aStd)
  //         var VA = new VAVector(v, a)
  //         Logger.debug(VA.toString())

  //         //add the vector to the list of vectors
  //         stringVAs = VA :: stringVAs
  //       }
        
  //     }
  //   }
  //   stringVA.Average(stringVAs)
  //   Logger.debug(stringVA.toString())
  //   if (count >= stringMinAmount){
  //     containsString = true
  //   }

  //   count = 0
  //   for (punct <- punctList){
  //     //Query tabel punctuationdictionary with punct
  //     DB.withConnection{ conn =>
  //     val stmt = conn.createStatement
  //       val rs = stmt.executeQuery("SELECT * FROM punctuation WHERE (Word = '" + punct + "')")

  //       //Test if it's a ANEW word...
  //        while(rs.next()){
  //         count += 1
  //           var vMean = rs.getDouble("VMean")
  //           var vStd = rs.getDouble("VSTD")
  //           var aMean = rs.getDouble("AMean")
  //           var aStd = rs.getDouble("ASTD")

  //           //temporary
  //           var v = vMean * (1.0/vStd)
  //           var a = aMean * (1.0/aStd)
  //           var VA = new VAVector(v, a)

  //           //add the vector to the list of vectors
  //           punctVAs = VA :: punctVAs
          
  //       }
  //     }
  //   }
  //   punctVA.Average(punctVAs)
  //   if (count >= punctMinAmount){
  //     containsPunct = true
  //   }

  //   count = 0
  //   for (emoji <- emojiList){
  //     //Query tabel emojidictionary with emoji
  //     DB.withConnection{ conn =>
  //     val stmt = conn.createStatement
  //       val rs = stmt.executeQuery("SELECT * FROM emoji WHERE (Word = '" + emoji + "')")

  //       //Test if it's a ANEW word...
  //        while(rs.next()){
  //         count += 1
  //           var vMean = rs.getDouble("VMean")
  //           var vStd = rs.getDouble("VSTD")
  //           var aMean = rs.getDouble("AMean")
  //           var aStd = rs.getDouble("ASTD")

  //           //temporary
  //           var v = vMean * (1.0/vStd)
  //           var a = aMean * (1.0/aStd)
  //           var VA = new VAVector(v, a)

  //           //add the vector to the list of vectors
  //           emojiVAs = VA :: emojiVAs
          
  //       }
  //     }
  //   }
  //   emojiVA.Average(emojiVAs)
  //   if (count >= emojiMinAmount){
  //     containsEmoji = true
  //   }


  //   count = 0
  //   for (emoticon <- emoticonList){
  //     //Query tabel emoticondictionary with emoticon
  //     var id = emoticon.substring(1, emoticon.length)
  //     Logger.debug(id)
  //     DB.withConnection{ conn =>
  //     val stmt = conn.createStatement
  //       val rs = stmt.executeQuery("SELECT * FROM textualemoticon WHERE (Word = '" + emoticon + "')")

  //       //Test if it's a ANEW word...
  //       while(rs.next()){
  //         count += 1
  //           var vMean = rs.getDouble("VMean")
  //           var vStd = rs.getDouble("VSTD")
  //           var aMean = rs.getDouble("AMean")
  //           var aStd = rs.getDouble("ASTD")

  //           //temporary
  //           var v = vMean * (1.0/vStd)
  //           var a = aMean * (1.0/aStd)
  //           var VA = new VAVector(v, a)

  //           //add the vector to the list of vectors
  //           emoticonVAs = VA :: emoticonVAs
          
  //       }
  //     }
  //   }
  //   emoticonVA.Average(emoticonVAs)
  //   if (count >= emoticonMinAmount){
  //     containsEmoticon = true
  //   }

  //   //---------------------------Calculate Message Vector-------------------------------
  //   //Scale vectors with settings
  //   var stringScaler: Double = stringWeight / (punctWeight * emojiWeight * emoticonWeight)
  //   var punctScaler: Double = punctWeight / (stringWeight * emojiWeight * emoticonWeight)
  //   var emojiScaler: Double = emojiWeight / (punctWeight * stringWeight * emoticonWeight)
  //   var emoticonScaler: Double = emoticonWeight / (punctWeight * emojiWeight * stringWeight)

  //   if(!containsString){ stringScaler = 0}
  //   if(!containsPunct){ punctScaler = 0}
  //   if(!containsEmoji){ emojiScaler = 0}
  //   if(!containsEmoticon){ emoticonScaler = 0}

  //   var totalScaler = stringScaler + punctScaler + emojiScaler + emoticonScaler

  //   stringVA.Multiply(stringScaler / totalScaler)
  //   punctVA.Multiply(punctScaler / totalScaler)
  //   emojiVA.Multiply(emojiScaler / totalScaler)
  //   emoticonVA.Multiply(emoticonScaler / totalScaler)

  //   var messageVAs = List[VAVector]()

  //   if(containsString){ messageVAs = stringVA :: messageVAs}
  //   if(containsPunct){ messageVAs = punctVA :: messageVAs}
  //   if(containsEmoji){ messageVAs = emojiVA :: messageVAs}
  //   if(containsEmoticon){ messageVAs = emoticonVA :: messageVAs}
  //   Logger.debug(messageVAs.toString())

  //   //Get result vector
  //   messageVA.Average(messageVAs)
  //   Logger.debug(messageVA.toString())
  // }
// }

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