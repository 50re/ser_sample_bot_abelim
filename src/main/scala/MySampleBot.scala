import MyButtons.{backBtn, supportBtn}
import MyConfig.supportChatId
import MyMenus.{mainMenuMarkup, suppportMenuMarkup}
import akka.actor.ActorSystem
import cats.syntax.functor._
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.{Commands, RegexCommands}
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.util.Try
import com.bot4s.telegram.methods.{ForwardMessage, SetChatMenuButton, SetMyCommands}
import com.bot4s.telegram.models.{BotCommand, MenuButtonDefault, User}
import sttp.client3.SttpBackend

import java.util.{Timer, TimerTask}

/**
 * Showcases different ways to declare commands (Commands + RegexCommands).
 *
 * Note that non-ASCII commands are not clickable.
 *
 * @param token Bot's token.
 */
abstract class ExampleBot(val token: String) extends TelegramBot {

  implicit val backend: SttpBackend[Future, Any] = SttpBackends.default
  override val client: RequestHandler[Future]    = new FutureSttpClient(token)
}
object Utils {
  def after[T](duration: Duration)(block: => T): Future[T] = {
    val promise = Promise[T]()
    val t       = new Timer()
    t.schedule(
      new TimerTask {
        override def run(): Unit =
          promise.complete(Try(block))
      },
      duration.toMillis
    )
    promise.future
  }
}


class MySampleBot(token: String)
  extends ExampleBot(token)
    with Polling
    with Commands[Future]
    with RegexCommands[Future] {

  var supportRequesters: List[User] = List.empty
  // Extractor
  object Int {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }

  val sys: ActorSystem = ActorSystem()
  //val master =

  request(
    SetMyCommands(
      List(
        BotCommand("start", "Начать работу с ботом")
      )
    )
  ).void


  onMessage { implicit msg => msg match {
    case _ if msg.text.get == "/start" =>
      reply(
        text = "Вы используете тестового бота на Bot4s",
        replyMarkup = Option(mainMenuMarkup)
      ).void
    case _ if msg.text.get == backBtn =>
      reply("Операция отменена",replyMarkup = Option(mainMenuMarkup)).void
    case _ if msg.text.get == supportBtn && !supportRequesters.contains(msg.from) =>
      supportRequesters = msg.from.get :: supportRequesters
      println("Accepted Support")
      reply(
        text = "Отправьте сообщение о проблеме для техподдержки",
        replyMarkup = Option(suppportMenuMarkup)
      ).void
    case _ if supportRequesters.contains(msg.from.get) && !(msg.text.get == backBtn) =>
      request(ForwardMessage(
        chatId = supportChatId,
        fromChatId = msg.chat.chatId,
        messageId = msg.messageId
      ))
      supportRequesters = supportRequesters.filterNot(_ == msg.from.get)
      reply(
        text = "✅ Операция выполнена успешно",
        replyMarkup = Option(mainMenuMarkup)
      ).void
  }}



}