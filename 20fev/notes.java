class Contador {
    private int valor = 0;

    public synchronized void incrementar() {
        valor++;
        System.out.println(Thread.currentThread().getName() + " incrementou para: " + valor);
    }

    public synchronized int getValor() {
        return valor;
    }
}

public class ConcorrenciaTeste {
    public static void main(String[] args) {
        Contador contador = new Contador();

        Runnable tarefa = () -> {
            for (int i = 0; i < 5; i++) {
                contador.incrementar();
                try {
                    Thread.sleep(100); // Simula algum processamento
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread t1 = new Thread(tarefa, "Thread-1");
        Thread t2 = new Thread(tarefa, "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Valor final do contador: " + contador.getValor());
    }
}
