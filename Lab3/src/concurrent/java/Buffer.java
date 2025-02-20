import java.util.LinkedList;

class Buffer {
    private final LinkedList<Integer> data = new LinkedList<>();
    private final int capacity = 100;

    public synchronized void put(int value) throws InterruptedException {
        while (data.size() >= capacity) {
            wait();  
        }
        data.add(value);
        System.out.println("Inserted: " + value + " | Buffer size: " + data.size());
        notifyAll(); 
    }

    public synchronized int remove() throws InterruptedException {
        while (data.isEmpty()) {
            wait();  
        }
        int value = data.removeFirst();
        System.out.println("Removed: " + value + " | Buffer size: " + data.size());
        notifyAll();  
        return value;
    }
}
