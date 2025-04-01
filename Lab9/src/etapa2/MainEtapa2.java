package etapa2;

import java.util.concurrent.*;
import java.util.Random;

class Produtor implements Runnable {
    private BlockingQueue<Integer> fila;
    private int tempo;
    private Random random = new Random();

    public Produtor(BlockingQueue<Integer> fila, int tempo) {
        this.fila = fila;
        this.tempo = tempo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) { 
            try {
                int numero = random.nextInt(10) + 1;
                fila.put(numero);
                System.out.println("Produtor gerou: " + numero);
                Thread.sleep(tempo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Consumidor implements Runnable {
    private BlockingQueue<Integer> fila;
    private int tempo;

    public Consumidor(BlockingQueue<Integer> fila, int tempo) {
        this.fila = fila;
        this.tempo = tempo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Integer numero = fila.poll(600, TimeUnit.MILLISECONDS); 
                if (numero == null) {
                    System.out.println("Consumidor parou: tempo limite atingido.");
                    break;
                }
                System.out.println("Consumidor processou: " + numero);
                Thread.sleep(tempo);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class MainEtapa2 {
    public static void main(String[] args) {
        BlockingQueue<Integer> fila = new ArrayBlockingQueue<>(10);
        Thread produtor = new Thread(new Produtor(fila, 500)); 
        Thread consumidor = new Thread(new Consumidor(fila, 700));
        
        produtor.start();
        consumidor.start();
    }
}
