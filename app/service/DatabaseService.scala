package service

import com.google.inject.Inject
import models.{Database, DatabaseList}

import scala.concurrent.Future


class DatabaseService @Inject() (items: DatabaseList) {

  def addItem(item: Database): Future[Database] = {
    items.add(item)
  }

  def deleteItem(id: Long, owner: String): Future[Int] = {
    items.delete(id, owner)
  }

  def getItem(id: Long): Future[Option[Database]] = {
    items.get(id)
  }

  def listAllItems(owner: String): Future[Seq[Database]] = {
    items.listAll(owner)
  }
}