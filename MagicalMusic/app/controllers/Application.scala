package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("This is our Magical Music"))
  }
  def about = Action{
  	Ok(views.html.about("About us"))
  }

}
