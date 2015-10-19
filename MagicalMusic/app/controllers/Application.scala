package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Logger
import scala.io.Source

class Application extends Controller 
{

  def index = Action {
    Ok(views.html.index("This is our Magical Music"))
  }

  def about = Action
  {
    var names = List[String]()
    DB.withConnection{ conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT artist from music")
      while (rs.next()) {
        names = rs.getString("artist") :: names
      }  
    }


    // ---------------------------------I used this code to preprocess, it gave a lot of problems when I wasn't working in the play framework------------------------
    // 
    // var id = "dsdf";

    // if (false){
    //   DB.withConnection{ conn =>
    //     val stmt = conn.createStatement
    //     val up = stmt.executeUpdate("UPDATE musiclyrics SET valenceLyrics = 0, arousalLyrics = 0;")
    //   }
    // }
    // var valuesUpdated = 0;
    // var count = 0;

    // //Foreach line in the file with lyrics data
    // for(line <- Source.fromFile("../data/Lyrics/mxm_dataset_trainOnlyEmo.txt").getLines())
    // {
    //   count += 1

    //   //For starting at a specific index
    //   if (count > 0){
    //     var stringVAs = List[VAVector]()
    //     var tokens = line.split(",")
    //     var first = true
    //     var found = false

    //     //Check if the lyrics are from a song in the database
    //     DB.withConnection{ conn =>
    //       val stmt = conn.createStatement
    //       var sqlString = "SELECT * FROM musiclyrics WHERE msdid ='" + tokens(0) + "';"
    //       val rs = stmt.executeQuery(sqlString)
    //       while (rs.next()) {
    //         found = true
    //         valuesUpdated += 1
    //         Logger.debug(valuesUpdated.toString)
    //       }
    //     }

    //     //If it's in the database calculate VA
    //     if(found)
    //     {
    //       for(t <- tokens)
    //       {
    //         var words = List[String]()

    //         if(!first)
    //         {
    //           var item = t.split(":")
    //           DB.withConnection{ conn =>
    //             val stmt = conn.createStatement
    //             val rs = stmt.executeQuery("SELECT * FROM textdictionary WHERE Word = \"" + item(0) + "\";")
    //             while(rs.next())
    //             {
    //               var vMean = rs.getDouble("VMean")
    //               var vStd = rs.getDouble("VSTD")
    //               var aMean = rs.getDouble("AMean")
    //               var aStd = rs.getDouble("ASTD")

    //               //temporary
    //               var v = vMean * (1.0/vStd)
    //               var a = aMean * (1.0/aStd)
    //               var VA = new VAVector(v, a)

    //               for(i <- 1 to item(1).toInt){
    //                 stringVAs = VA :: stringVAs
    //               }
                    
    //             }
    //           }
    //         }
    //         first = false
    //       } 
    //     }
    //     //Update the VA values of the song in the database
    //     var totalVA = new VAVector(0.0, 0.0)
    //     if (stringVAs.length != 0)
    //       totalVA.Average(stringVAs)

    //     DB.withConnection{ conn =>
    //       val stmt = conn.createStatement
    //       var sqlString = "UPDATE musiclyrics SET valenceLyrics = " + totalVA.v + ", arousalLyrics = " + totalVA.a + " WHERE msdid ='" + tokens(0) + "';"
    //       Logger.debug(count + ": " + sqlString)
    //       val rs = stmt.executeUpdate(sqlString)
    //     }
    //   }
    //   else
    //     Logger.debug(count + ": skipped")
    // }

    Ok(views.html.about(names))
  }
}
