import java.util.concurrent.*;
import java.util.Random;

class Produtor implements Runnable {
    private BlockingQueue<Integer> fila;
    private Random random = new Random();

    public Produtor(BlockingQueue<Integer> fila) {
        this.fila = fila;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int numero = random.nextInt(10) + 1;
                fila.put(numero);
                System.out.println("Produtor gerou: " + numero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class Consumidor implements Runnable {
    private BlockingQueue<Integer> fila;

    public Consumidor(BlockingQueue<Integer> fila) {
        this.fila = fila;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int numero = fila.take();
                System.out.println("Consumidor processou " + numero);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}


public class MainEtapa1 {
    public static void main(String[] args) {
        BlockingQueue<Integer> fila = new ArrayBlockingQueue<>(10);
        Thread produtor = new Thread(new Produtor(fila)); 
        Thread consumidor = new Thread(new Consumidor(fila));
        
        produtor.start();
        consumidor.start();
    }
}

