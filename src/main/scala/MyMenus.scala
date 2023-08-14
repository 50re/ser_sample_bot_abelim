import MyButtons.{backBtn, menuBtn, profileBtn, supportBtn}
import com.bot4s.telegram.models.{KeyboardButton, ReplyKeyboardMarkup, ReplyMarkup}
import com.bot4s.telegram.models._

object MyMenus {
  val mainMenuMarkup: ReplyMarkup = ReplyKeyboardMarkup(
    Seq(
      Seq(
        KeyboardButton(text = menuBtn)
      ),
      Seq(
        KeyboardButton(text = profileBtn), KeyboardButton(text = supportBtn)
      )
    )
  )
  val suppportMenuMarkup: ReplyMarkup = ReplyKeyboardMarkup.singleButton(KeyboardButton(backBtn))
}
