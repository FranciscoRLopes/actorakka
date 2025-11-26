
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class AkkaMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("KnapsackGASystem");

        int nWorkers = Runtime.getRuntime().availableProcessors();

        ActorRef controller =
                system.actorOf(GAController.props(nWorkers), "gaController");


        controller.tell(new GAProtocol.Start(), ActorRef.noSender());

    }
}