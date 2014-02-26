package  com.kifi.franz

import play.api.libs.json.JsValue
import play.api.libs.iteratee.Enumerator

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.FiniteDuration



case class QueueName(name: String)

case class MessageId(id: String)


trait SQSMessage[T] {
  val body: T
  val attributes: Map[String,String]
  val consume: () => Unit
}
case class SQSStringMessage(body: String, consume: () => Unit, attributes: Map[String, String] = Map.empty) extends SQSMessage[String]
case class SQSJsonMessage(body: JsValue, consume: () => Unit, attributes: Map[String, String] = Map.empty) extends SQSMessage[JsValue]



trait SQSQueue {
  def send(msg: String)(implicit ec: ExecutionContext): Future[MessageId]
  def send(msg: JsValue)(implicit ec: ExecutionContext): Future[MessageId]

  def nextString(implicit ec: ExecutionContext): Future[Option[SQSStringMessage]]
  def nextStringWithLock(lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Future[Option[SQSStringMessage]]
  def nextStringBatch(maxBatchSize: Int)(implicit ec: ExecutionContext): Future[Seq[SQSStringMessage]]
  def nextStringBatchWithLock(maxBatchSize: Int, lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Future[Seq[SQSStringMessage]]

  def nextJson(implicit ec: ExecutionContext): Future[Option[SQSJsonMessage]]
  def nextJsonWithLock(lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Future[Option[SQSJsonMessage]]
  def nextJsonBatch(maxBatchSize: Int)(implicit ec: ExecutionContext): Future[Seq[SQSJsonMessage]]
  def nextJsonBatchWithLock(maxBatchSize: Int, lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Future[Seq[SQSJsonMessage]]

  def stringEnumerator(implicit ec: ExecutionContext) : Enumerator[SQSStringMessage]
  def stringEnumeratorWithLock(lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Enumerator[SQSStringMessage]

  def jsonEnumerator(implicit ec: ExecutionContext): Enumerator[SQSJsonMessage]
  def jsonEnumeratorWithLock(lockTimeout: FiniteDuration)(implicit ec: ExecutionContext): Enumerator[SQSJsonMessage]
}