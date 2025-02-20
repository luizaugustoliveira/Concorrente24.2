import java.util.LinkedList;

class Buffer {
    private final LinkedList<Integer> data = new LinkedList<>();
    private final int capacity = 100;

    public synchronized void put(int value) throws InterruptedException {
        while (data.size() >= capacity) {
            wait();  // Espera se o buffer estiver cheio
        }
        data.add(value);
        System.out.println("Inserted: " + value + " | Buffer size: " + data.size());
        notifyAll();  // Notifica os consumidores que há itens disponíveis
    }

    public synchronized int remove() throws InterruptedException {
        while (data.isEmpty()) {
            wait();  // Espera se o buffer estiver vazio
        }
        int value = data.removeFirst();
        System.out.println("Removed: " + value + " | Buffer size: " + data.size());
        notifyAll();  // Notifica os produtores que há espaço disponível
        return value;
    }
}

class Producer implements Runnable {
    private final Buffer buffer;
    private final int maxItems;
    private final int sleepTime;
    private final int id;

    public Producer(int id, Buffer buffer, int maxItems, int sleepTime) {
        this.id = id;
        this.buffer = buffer;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < maxItems; i++) {
                Thread.sleep(sleepTime);
                int item = (int) (Math.random() * 100);
                System.out.println("Producer " + id + " produced item " + item);
                buffer.put(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;

    public Consumer(int id, Buffer buffer, int sleepTime) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int item = buffer.remove();
                System.out.println("Consumer " + id + " consumed item " + item);
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Use: java Main <num_producers> <max_items_per_producer> <producing_time> <num_consumers> <consuming_time>");
            return;
        }

        int numProducers = Integer.parseInt(args[0]);
        int maxItemsPerProducer = Integer.parseInt(args[1]);
        int producingTime = Integer.parseInt(args[2]);
        int numConsumers = Integer.parseInt(args[3]);
        int consumingTime = Integer.parseInt(args[4]);

        Buffer buffer = new Buffer();

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(new Producer(i + 1, buffer, maxItemsPerProducer, producingTime));
            producers[i].start();
        }

        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(new Consumer(i + 1, buffer, consumingTime));
            consumers[i].start();
        }

        for (Thread producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                producer.interrupt();
            }
        }

        System.out.println("All producers finished. Consumers will stop.");
        System.exit(0);
    }
}