import scala.util.matching.Regex

object MyRegex {
  val slashCommand: Regex = """^/(\w+)$""".r
  val emojiCommand: Regex = """([^[:alnum:]]+) (\w+)$""".r
}
