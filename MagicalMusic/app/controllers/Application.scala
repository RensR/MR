package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("This is our Magical Music"))
  }
  def about = Action{
      var names = List[String]()
  	  DB.withConnection{ conn =>
    	val stmt = conn.createStatement
      	val rs = stmt.executeQuery("SELECT artist from music")
      	while (rs.next()) {

      		names = rs.getString("artist") :: names
    	}	
	}

  	Ok(views.html.about(names))
  }
}
