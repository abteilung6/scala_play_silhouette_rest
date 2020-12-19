package service

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, ExecutionContext}
import slick.jdbc.MySQLProfile.api._


import scala.concurrent.duration.Duration


class DatabaseConnectorService @Inject()(
                                          protected val dbConfigProvider: DatabaseConfigProvider
                                        )(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  def databaseExists(databaseName: String): Boolean = {
    var result = false
    try {
      val query = Await.result(dbConfig.db.run(sql"""SHOW DATABASES LIKE ${databaseName}""".as[(String)]), Duration.Inf)
      if (query.nonEmpty && query.contains("hello3")) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }

  def createDatabase(databaseName: String): Boolean = {
    var result = false
    try {
      val query = Await.result(dbConfig.db.run(sql"""CREATE DATABASE IF NOT EXISTS #$databaseName""".as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(1)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }

  def deleteDatabase(databaseName: String): Boolean = {
    var result = false
    try {
      val query = Await.result(dbConfig.db.run(sql"""DROP DATABASE IF EXISTS #$databaseName""".as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(1)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result

  }
}
