import MyButtons.{backBtn, supportBtn}
import MyConfig.supportChatId
import MyMenus.{mainMenuMarkup, suppportMenuMarkup}
import cats.syntax.functor._
import com.bot4s.telegram.api.declarative.{Action, Commands, Messages, RegexCommands}
import com.bot4s.telegram.future.Polling

import scala.concurrent.Future
import scala.util.Try
import com.bot4s.telegram.methods.{ForwardMessage, SendMessage, SetMyCommands}
import com.bot4s.telegram.models.{BotCommand, Message}

class MySampleBot(token: String)
  extends AkkaExampleBot(token)
    with Polling
    with Commands[Future]
    with RegexCommands[Future] {
  private var state = new MyBotState
  object Int {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }

  request(
    SetMyCommands(
      List(
        BotCommand("start", "Начать работу с ботом")
      )
    )
  ).void

  onMessage { implicit msg => msg.text.get match {
    case "/start" =>
      reply(
        text = "Вы используете тестового бота на Bot4s",
        replyMarkup = Option(mainMenuMarkup)
      ).void
    case `backBtn` =>
      state.cancelSupport(msg.from.get)
      println(s"deleted support ${msg.from.get}, state: ${state.supportRequesters.length}")
      reply("Операция отменена", replyMarkup = Option(mainMenuMarkup)).void

    case `supportBtn` =>
      state.requestSupport(msg.from.get)
      println(s"requested support ${msg.from.get}, state: ${state.supportRequesters.length}")
      println("Accepted Support")
      reply(
        text = "Отправьте сообщение о проблеме для техподдержки",
        replyMarkup = Option(suppportMenuMarkup)
      ).void

    case _ => handleMessage()
  }}

  private def handleMessage()(implicit msg: Message): Future[Unit] = msg match {
    case _ if state.isSupportRequester(msg.from.get) =>
      request(ForwardMessage(
        chatId = supportChatId,
        fromChatId = msg.chat.chatId,
        messageId = msg.messageId
      ))
      state.cancelSupport(msg.from.get)
      reply(
        text = "✅ Операция выполнена успешно",
        replyMarkup = Option(mainMenuMarkup)
      ).void
    case _ => reply(
        text = "Команда не распознана или пока не реализована",
        replyMarkup = Option(mainMenuMarkup)
      ).void
  }
}
