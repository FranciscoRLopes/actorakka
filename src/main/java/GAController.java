
import java.util.Random;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class GAController extends AbstractActor {

    private static final int N_GENERATIONS = 500;
    private static final int POP_SIZE = 100000;
    private static final double PROB_MUTATION = 0.5;
    private static final int TOURNAMENT_SIZE = 3;

    private final Random r = new Random();

    private Individual[] population = new Individual[POP_SIZE];

    private final ActorRef fitnessRouter;

    private int generation = 0;
    private int evaluatedCount = 0;

    // Fábrica de Props para criar o GAController com N workers
    public static Props props(int nWorkers) {
        return Props.create(GAController.class, () -> new GAController(nWorkers));
    }

    private GAController(int nWorkers) {
        // cria população inicial
        populateInitialPopulationRandomly();

        // cria um router de fitness workers (task parallelism)
        this.fitnessRouter = getContext().actorOf(
                new RoundRobinPool(nWorkers)
                        .props(Props.create(FitnessWorker.class)),
                "fitnessRouter");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GAProtocol.Start.class, this::onStart)
                .match(GAProtocol.Evaluated.class, this::onEvaluated)
                .build();
    }

    private void populateInitialPopulationRandomly() {
        for (int i = 0; i < POP_SIZE; i++) {
            population[i] = Individual.createRandom(r);
        }
    }

    private void onStart(GAProtocol.Start msg) {
        generation = 0;
        evaluateCurrentPopulation();
    }

    private void evaluateCurrentPopulation() {
        evaluatedCount = 0;
        // manda avaliar todos os indivíduos em paralelo
        for (Individual ind : population) {
            fitnessRouter.tell(new GAProtocol.Evaluate(ind), getSelf());
        }
    }

    private void onEvaluated(GAProtocol.Evaluated msg) {
        // aqui o indivíduo já tem fitness calculado
        evaluatedCount++;

        if (evaluatedCount == POP_SIZE) {
            // todos avaliados -> podemos passar para seleção / cruzamento / mutação
            Individual best = bestOfPopulation();
            System.out.println("Best at generation " + generation + " is "
                    + best + " with " + best.fitness);

            generation++;

            if (generation >= N_GENERATIONS) {
                System.out.println("Finished GA. Best solution: "
                        + best + " with " + best.fitness);
                getContext().getSystem().terminate();
            } else {
                // criar nova população a partir da atual
                createNextGeneration(best);
                // e voltamos a avaliar
                evaluateCurrentPopulation();
            }
        }
    }

    private void createNextGeneration(Individual best) {
        Individual[] newPopulation = new Individual[POP_SIZE];

        // elitismo: o melhor passa direto
        newPopulation[0] = best;

        // resto da população
        for (int i = 1; i < POP_SIZE; i++) {
            Individual parent1 = tournament(TOURNAMENT_SIZE, r);
            Individual parent2 = tournament(TOURNAMENT_SIZE, r);

            Individual child = parent1.crossoverWith(parent2, r);

            if (r.nextDouble() < PROB_MUTATION) {
                child.mutate(r);
            }

            newPopulation[i] = child;
        }

        this.population = newPopulation;
    }

    private Individual tournament(int tournamentSize, Random r) {
        /*
         * Igual ao teu KnapsackGA: escolhe tournamentSize indivíduos aleatórios
         * e fica com o melhor.
         */
        Individual best = population[r.nextInt(POP_SIZE)];
        for (int i = 0; i < tournamentSize; i++) {
            Individual other = population[r.nextInt(POP_SIZE)];
            if (other.fitness > best.fitness) {
                best = other;
            }
        }
        return best;
    }

    private Individual bestOfPopulation() {
        /*
         * Igual ao teu KnapsackGA.bestOfPopulation().
         */
        Individual best = population[0];
        for (Individual other : population) {
            if (other.fitness > best.fitness) {
                best = other;
            }
        }
        return best;
    }
}
