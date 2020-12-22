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

  /**
   * Checks if database exists
   * @param databaseName name of database
   * @return true if database exists
   */
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

  /**
   * Create a database
   * @param databaseName the name of database
   * @return true if the database was created
   */
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

  /**
   * Deletes a database if the database exists
   * @param databaseName the name of the database
   * @return true if database was deleted
   */
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

  /**
   * Checks if username exists
   * @param username the username
   * @return true if username exists
   */
  def userExists(username: String): Boolean = {
    var result = false;
    try {
      val query = Await.result(dbConfig.db.run(sql"""SELECT COUNT(*) FROM mysql.user where user=${username}""".as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(1)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }

  /**
   * Creates a databse user
   * @param username the name of user
   * @param password the password of user
   * @return true  if user was created
   */
  def createUser(username: String, password: String): Boolean = {
    var result = false
    try {
      val query = Await.result(dbConfig.db.run(sql"""CREATE USER ${username} IDENTIFIED BY ${password}""".as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(0)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }

  /**
   * Grants user access to a database
   * @param databaseName name of database
   * @param username name of user
   * @return true if database granted access to user
   */
  def databaseGrantUser(databaseName: String, username: String): Boolean = {
    var result = false;
    val statement = sql"""GRANT ALL PRIVILEGES ON #${databaseName}.* TO ${username}"""
    try {
      val query = Await.result(dbConfig.db.run(statement.as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(0)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }

  /**
   * Flush privileges
   * @return true if success
   */
  def flushPrivileges(): Boolean = {
    var result = false;
    val statement = sql"""FLUSH PRIVILEGES"""
    try {
      val query = Await.result(dbConfig.db.run(statement.as[(Int)]), Duration.Inf)
      if (query.nonEmpty && query.contains(0)) {
        result = true
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return result
  }
}
