//#full-example
package AkkaStart

import AkkaStart.GreeterMain.SayHello
import akka.actor.typed.ActorSystem

//#main-class
object AkkaQuickstart extends App {
  //#actor-system
  val greeterMain: ActorSystem[GreeterMain.SayHello] =
    ActorSystem(GreeterMain(), "SomeSystemName")
  //#actor-system

  //#main-send-messages
  greeterMain ! SayHello("Ser")
  //#main-send-messages
}
//#main-class
//#full-example