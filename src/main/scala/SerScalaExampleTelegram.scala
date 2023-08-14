import MyConfig.token

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object SerScalaExampleTelegram extends App {
  val bot = new CommandsBot(token)
  val eol = bot.run()
  println("Press [ENTER] to shutdown the bot, it may take a few seconds...")
  scala.io.StdIn.readLine()
  bot.shutdown() // initiate shutdown
  // Wait for the bot end-of-life
  Await.result(eol, Duration.Inf)
}
