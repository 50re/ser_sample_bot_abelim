import MyButtons.supportBtn
import MyConfig.supportChatId
import MyMenus.{mainMenuMarkup, suppportMenuMarkup}
import akka.actor.ActorSystem
import cats.syntax.functor._
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.{CommandFilterMagnet, Commands, RegexCommands}
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.util.Try
import com.bot4s.telegram.methods.{ForwardMessage, SendMessage, SetMyCommands}
import com.bot4s.telegram.models.{BotCommand, MenuButton, MenuButtonDefault, User}
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


class CommandsBot(token: String)
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
    case _ if msg.text.get.equals("/start") => reply(
      text = "Вы используете тестового бота на Bot4s",
      replyMarkup = Option(mainMenuMarkup)
    ).void
    case _ if msg.text.get.equals(supportBtn) && !supportRequesters.contains(msg.from) =>
      supportRequesters = msg.from.get :: supportRequesters
      println("Accepted Support")
      reply(
        text = "Отправьте сообщение о проблеме для техподдержки",
        replyMarkup = Option(suppportMenuMarkup)
      ).void
    case _ if supportRequesters.contains(msg.from.get) =>
      request(ForwardMessage(
        chatId = supportChatId,
        fromChatId = msg.chat.chatId,
        messageId = msg.messageId
      ))
      supportRequesters = supportRequesters.filterNot(_.equals(msg.from.get))
      reply(
        text = "✅ Операция выполнена успешно",
        replyMarkup = Option(mainMenuMarkup)
      ).void
  }}

  // String commands.
  onCommand("/hello") { implicit msg =>
    reply("Hello America!").void
  }

  // '/' prefix is optional
  onCommand("hola") { implicit msg =>
    reply("Hola Mundo!").void
  }

  // Several commands can share the same handler.
  // Shows the 'using' extension to extract information from messages.
  onCommand("/hallo" | "/bonjour" | "/ciao" | "/hola") { implicit msg =>
    using(_.from) { // sender
      user =>
        reply(s"Hello ${user.firstName} from Europe?").void
    }
  }

  // Also using Symbols; the "/" prefix is added by default.
  onCommand("привет") { implicit msg =>
    reply("\uD83C\uDDF7\uD83C\uDDFA").void
  }

  // Note that non-ascii commands are not clickable.
  onCommand("こんにちは" | "你好" | "안녕하세요") { implicit msg =>
    reply("Hello from Asia?").void
  }

  // Different spellings + emoji commands.

  onCommand("/metro" | "/métro" | "/🚇") { implicit msg =>
    reply("Metro schedule bla bla...").void
  }

  onCommand("beer" | "beers" | "🍺" | "🍻") { implicit msg =>
    reply("Beer menu bla bla...").void
  }

  // withArgs extracts command arguments.
  onCommand("echo") { implicit msg =>
    withArgs { args =>
      reply(args.mkString(" ")).void
    }
  }

  // withArgs with pattern matching.
  onCommand("/inc") { implicit msg =>
    withArgs {
      case Seq(Int(i)) =>
        reply("" + (i + 1)).void

      // Conveniently avoid MatchError, providing hints on usage.
      case _ =>
        reply("Invalid argument. Usage: /inc 123").void
    }
  }

  // Regex commands also available.
  onRegex("""/timer\s+([0-5]?[0-9]):([0-5]?[0-9])""".r) { implicit msg =>
  { case Seq(Int(mm), Int(ss)) =>
    reply(s"Timer set: $mm minute(s) and $ss second(s)").void
    Utils.after(mm.minutes + ss.seconds) {
      reply("Time's up!!!")
    }
  }
  }
}