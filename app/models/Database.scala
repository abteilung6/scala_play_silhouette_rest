package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.MySQLProfile.api._

case class Database(id: Long, name: String, engine: String, status: String)

case class DatabaseFormData(name: String, engine: String, status: String)

object DatabaseForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "engine" -> nonEmptyText,
      "status" -> nonEmptyText,
    )(DatabaseFormData.apply)(DatabaseFormData.unapply)
  )
}

class DatabaseTableDef(tag: Tag) extends Table[Database](tag, "database") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def engine = column[String]("engine")

  def status = column[String]("status")

  override def * = (id, name, engine, status) <> (Database.tupled, Database.unapply)
}


class DatabaseList @Inject()(
                              protected val dbConfigProvider: DatabaseConfigProvider
                            )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  var databaseList = TableQuery[DatabaseTableDef]

  def add(databaseItem: Database): Future[Database] = {
    val insertQuery = databaseList returning databaseList.map(_.id) into ((item, id) => item.copy(id = id))
    val action = insertQuery += databaseItem

    dbConfig.db.run(action).map(d => d)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(databaseList.filter(_.id === id).delete)
  }

  def update(id: Long, databaseItem: Database): Future[Int] = {
    dbConfig.db
      .run(
        databaseList.filter(_.id === id)
        .map(x => (x.name, x.engine, x.status))
        .update(databaseItem.name, databaseItem.engine, databaseItem.status)
      )
  }

  def get(id: Long): Future[Option[Database]] = {
    dbConfig.db.run(databaseList.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Database]] = {
    dbConfig.db.run(databaseList.result)
  }
}