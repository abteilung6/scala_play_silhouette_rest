package controllers.api

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{Database, DatabaseForm}
import play.api.data.FormError
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import services.DatabaseService
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseController @Inject()(
                                    cc: ControllerComponents,
                                    databaseService: DatabaseService,
                                    silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc) {

  implicit val databaseFormat = Json.format[Database]

  def getAll(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    databaseService.listAllItems map { items =>
      print(request.identity)
      Ok(Json.toJson(items))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    databaseService.getItem(id) map { item =>
      Ok(Json.toJson(item))
    }
  }

  def add(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    DatabaseForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val newDatabaseItem = Database(0, data.name, data.engine, data.status)
        databaseService.addItem(newDatabaseItem).map(database => Ok(Json.toJson(database)))
      })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    DatabaseForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val databaseItem = Database(id, data.name, data.engine, data.status)
        databaseService.updateItem(id, databaseItem).map(_ => Ok(Json.toJson(databaseItem)))
      })
  }

  def delete(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    databaseService.deleteItem(id) map { res =>
      Ok("")
    }
  }
}