package controllers.api

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{Database, DatabaseForm}
import play.api.data.FormError
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import service.DatabaseService
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
    val owner: String = request.authenticator.loginInfo.providerKey
    databaseService.listAllItems(owner) map { items =>
      Ok(Json.toJson(items))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    databaseService.getItem(id) map { item =>
      Ok(Json.toJson(item))
    }
  }

  def add(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    DatabaseForm.form.bindFromRequest.fold(
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val owner: String = request.authenticator.loginInfo.providerKey
        val engine: String = "PostgreSQL"
        val status: String = "Available"
        val newDatabaseItem = Database(0, data.name, engine, status, owner)
        databaseService.addItem(newDatabaseItem).map(database => Ok(Json.toJson(database)))
      })
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val owner: String = request.authenticator.loginInfo.providerKey
    databaseService.deleteItem(id, owner) map { res =>
      Ok(Json.toJson("{}"))
    }
  }
}