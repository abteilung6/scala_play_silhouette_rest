package services

import com.google.inject.Inject
import models.{Database, DatabaseList}

import scala.concurrent.Future

class DatabaseService @Inject() (items: DatabaseList) {

  def addItem(item: Database): Future[Database] = {
    items.add(item)
  }

  def deleteItem(id: Long): Future[Int] = {
    items.delete(id)
  }

  def updateItem(id: Long, item: Database): Future[Int] = {
    items.update(id, item)
  }

  def getItem(id: Long): Future[Option[Database]] = {
    items.get(id)
  }

  def listAllItems: Future[Seq[Database]] = {
    items.listAll
  }
}