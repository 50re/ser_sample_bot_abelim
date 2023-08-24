import MyButtons.{backBtn, menuBtn, profileBtn, supportBtn}
import com.bot4s.telegram.models.{KeyboardButton, ReplyKeyboardMarkup, ReplyMarkup}
import com.bot4s.telegram.models._

object MyMenus {
  val contextMenuMarkup: ReplyMarkup = ReplyKeyboardMarkup(
    Seq(
      Seq(
        KeyboardButton(text = menuBtn)
      ),
      Seq(
        KeyboardButton(text = profileBtn), KeyboardButton(text = supportBtn)
      )
    )
  )
  val mainMenuMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup(
    Seq(
      Seq(
        //InlineKeyboardButton(text = "what",)
      )
    )
  )
  val supportMenuMarkup: ReplyMarkup = ReplyKeyboardMarkup.singleButton(KeyboardButton(backBtn))
}
