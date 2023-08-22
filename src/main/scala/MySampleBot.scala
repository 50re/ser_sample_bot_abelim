import MyButtons.{backBtn, menuBtn, supportBtn}
import MyConfig.supportChatId
import MyMenus.{contextMenuMarkup, supportMenuMarkup}
import MyRegex.{emojiCommand, slashCommand}
import cats.syntax.functor._
import com.bot4s.telegram.api.declarative.{Action, Commands, Messages, RegexCommands}
import com.bot4s.telegram.future.Polling

import scala.concurrent.Future
import scala.util.Try
import com.bot4s.telegram.methods.{ForwardMessage, SendMessage, SetMyCommands}
import com.bot4s.telegram.models.{BotCommand, Message, User}

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

  onMessage { implicit msg =>
    if (msg.text.get != "")
      msg.text.get match {
        case slashCommand(cmd) =>
          handleSlashCommands(cmd)
        case emojiCommand(_,cmd) =>
          handleIconCommands(cmd)
        case _ => handleMessage()
      }
    else Future()
  }

  private def handleSlashCommands(cmd:String)(implicit msg: Message):
  Future[Unit] = cmd match {
    case "start" =>
      reply(
        text = "Вы используете тестового бота на Bot4s",
        replyMarkup = Option(contextMenuMarkup)
      ).void
  }

  private def handleIconCommands(cmd: String)(implicit msg: Message):
  Future[Unit] = cmd match {
    case backBtn.text =>
      state.cancelSupport(msg.from.get)
      println(s"deleted support ${msg.from.get}, state: ${state.supportRequesters.length}")
      reply("Операция отменена", replyMarkup = Option(contextMenuMarkup)).void

    case supportBtn.text =>
      state.requestSupport(msg.from.get)
      println(s"requested support ${msg.from.get}, state: ${state.supportRequesters.length}")
      println("Accepted Support")
      reply(
        text = "Отправьте сообщение о проблеме для техподдержки",
        replyMarkup = Option(supportMenuMarkup)
      ).void
    case menuBtn.text =>
      userMenuHandler()

  }
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
        replyMarkup = Option(contextMenuMarkup)
      ).void
    case _ => reply(
        text = "Команда не распознана или пока не реализована",
        replyMarkup = Option(contextMenuMarkup)
      ).void
  }

  private def userMenuHandler()(implicit msg: Message):
  Future[Unit] = {
    reply(
      text = "Меню (заглушка)",
      replyMarkup =
    )
  }

}
