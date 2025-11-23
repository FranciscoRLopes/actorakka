public class Main {
	public static void main(String[] args) {
		KnapsackGA ga = new KnapsackGA();
        long start = System.nanoTime();
		ga.run();
        long end = System.nanoTime();
        System.out.println("Tempo: " + (end - start) / 1_000_000 + " ms");
	}
}
