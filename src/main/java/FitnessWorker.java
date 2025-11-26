

import akka.actor.AbstractActor;

public class FitnessWorker extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GAProtocol.Evaluate.class, this::onEvaluate)
                .build();
    }

    private void onEvaluate(GAProtocol.Evaluate msg) {

        msg.individual.measureFitness();


        getSender().tell(new GAProtocol.Evaluated(msg.individual), getSelf());
    }
}