import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.*;

public class ConcurrentSum {
    private static final Map<Long, List<String>> sumMap = new ConcurrentHashMap<>();
    private static final Object lock = new Object();
    private static long totalSum = 0;

    public static int sum(FileInputStream fis) throws IOException {
        int byteRead;
        int sum = 0;
        while ((byteRead = fis.read()) != -1) {
            sum += byteRead;
        }
        return sum;
    }

    public static long sum(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.isRegularFile(filePath)) {
            try (FileInputStream fis = new FileInputStream(filePath.toString())) {
                return sum(fis);
            }
        } else {
            throw new RuntimeException("Non-regular file: " + path);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java ConcurrentSum filepath1 filepath2 filepathN");
            System.exit(1);
        }

        int n = args.length;
        int maxThreads = Math.max(1, n / 2);
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<Future<Map.Entry<String, Long>>> futures = new ArrayList<>();

        for (String path : args) {
            futures.add(executor.submit(() -> {
                long fileSum = sum(path);
                System.out.println(path + " : " + fileSum);
                
                synchronized (lock) {
                    totalSum += fileSum;
                }
                
                sumMap.computeIfAbsent(fileSum, k -> Collections.synchronizedList(new ArrayList<>())).add(path);
                return Map.entry(path, fileSum);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        
        System.out.println("Total Sum: " + totalSum);

        for (Map.Entry<Long, List<String>> entry : sumMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey() + " " + String.join(" ", entry.getValue()));
            }
        }
    }
}
