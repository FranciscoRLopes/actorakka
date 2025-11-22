
public class GAProtocol {

    // Mensagem para iniciar o algoritmo
    public static final class Start {
        // vazio de propósito
    }

    // Pedido de avaliação de fitness
    public static final class Evaluate {
        public final Individual individual;

        public Evaluate(Individual individual) {
            this.individual = individual;
        }
    }

    // Resposta com fitness calculado
    public static final class Evaluated {
        public final Individual individual;

        public Evaluated(Individual individual) {
            this.individual = individual;
        }
    }
}