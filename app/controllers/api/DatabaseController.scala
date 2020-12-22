package controllers.api

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{Database, DatabaseForm}
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import service.DatabaseService
import service.DatabaseConnectorService
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import scala.concurrent.{Await}


class DatabaseController @Inject()(
                                    cc: ControllerComponents,
                                    databaseService: DatabaseService,
                                    databaseConnectorService: DatabaseConnectorService,
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
        val name: String = owner + "_" + data.name
        val engine: String = "PostgreSQL"
        val status: String = "Available"
        val newDatabaseItem = Database(0, name, engine, status, owner)
        databaseConnectorService.createDatabase(name)
        databaseConnectorService.databaseGrantUser(name, owner)
        databaseService.addItem(newDatabaseItem).map(database => Ok(Json.toJson(database)))
      })
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    val owner: String = request.authenticator.loginInfo.providerKey
    val item = databaseService.getItem(id)
    val result = Await.result(item, Duration.Inf).get
    if (result.owner == owner) {
      databaseConnectorService.deleteDatabase(result.name)
      databaseService.deleteItem(id, owner)
    }
    Future {
      Ok(Json.toJson("{}"))
    }
  }
}