import com.bot4s.telegram.models.KeyboardButton

import scala.language.implicitConversions

object MyButtons {
  case class MyButton(icon: String, text: String)
  implicit def myBtnToString(btn:MyButton): String =
    btn.icon + " " + btn.text
  val menuBtn: MyButton = MyButton("≡","Показать меню")
  val profileBtn: MyButton = MyButton("👤","Профиль")
  val supportBtn:MyButton = MyButton("💬","Поддержка")
  val backBtn:MyButton = MyButton("⬅","Назад")
  val settingsBtn = ""
}
