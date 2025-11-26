
public class GAProtocol {

    // Mensagem para iniciar o algoritmo
    public static final class Start {

    }

    public static final class Evaluate {
        public final Individual individual;

        public Evaluate(Individual individual) {
            this.individual = individual;
        }
    }


    public static final class Evaluated {
        public final Individual individual;

        public Evaluated(Individual individual) {
            this.individual = individual;
        }
    }
}