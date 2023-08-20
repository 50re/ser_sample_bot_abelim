import com.bot4s.telegram.models.User

class MyBotState {
  var supportRequesters:Vector[User] = Vector.empty
  def requestSupport: User => Unit = user => supportRequesters = supportRequesters :+ user
  def cancelSupport: User => Unit = user => supportRequesters = supportRequesters.filterNot(user.==)
  def isSupportRequester: User => Boolean = user => supportRequesters.contains(user)
}
