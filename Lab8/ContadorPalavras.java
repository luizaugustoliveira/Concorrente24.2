import java.io.*;
import java.util.concurrent.*;

public class ContadorPalavras {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Nenhum arquivo especificado.");
            return;
        }
        
        int totalPalavras = 0;
        for (String arquivo : args) {
            try {
                int palavras = contarPalavras(arquivo);
                System.out.println("Arquivo: " + arquivo + " - Palavras: " + palavras);
                totalPalavras += palavras;
            } catch (IOException e) {
                System.err.println("Erro ao ler o arquivo: " + arquivo);
            }
        }
        System.out.println("Total de palavras: " + totalPalavras);
    }

    static int contarPalavras(String nomeArquivo) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(nomeArquivo));
        int count = 0;
        String linha;
        while ((linha = br.readLine()) != null) {
            count += linha.split("\\s+").length;
        }
        br.close();
        return count;
    }
}

class ContadorPalavras2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Nenhum arquivo especificado.");
            return;
        }

        int totalArquivos = args.length;
        CountDownLatch latch = new CountDownLatch(totalArquivos);
        AtomicInteger totalPalavras = new AtomicInteger(0);

        for (String arquivo : args) {
            new Thread(() -> {
                try {
                    int palavras = ContadorPalavras.contarPalavras(arquivo);
                    System.out.println("Arquivo: " + arquivo + " - Palavras: " + palavras);
                    totalPalavras.addAndGet(palavras);
                } catch (IOException e) {
                    System.err.println("Erro ao ler o arquivo: " + arquivo);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
            System.out.println("Total de palavras: " + totalPalavras.get());
        } catch (InterruptedException e) {
            System.err.println("Execução interrompida.");
        }
    }
}

class ContadorPalavras3 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Nenhum arquivo especificado.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger totalPalavras = new AtomicInteger(0);

        for (String arquivo : args) {
            executor.execute(() -> {
                try {
                    int palavras = ContadorPalavras.contarPalavras(arquivo);
                    System.out.println("Arquivo: " + arquivo + " - Palavras: " + palavras);
                    totalPalavras.addAndGet(palavras);
                } catch (IOException e) {
                    System.err.println("Erro ao ler o arquivo: " + arquivo);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execução interrompida.");
        }
        System.out.println("Total de palavras: " + totalPalavras.get());
    }
}

class ContadorPalavras4 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Nenhum arquivo especificado.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Integer>> resultados = new ArrayList<>();

        for (String arquivo : args) {
            resultados.add(executor.submit(() -> ContadorPalavras.contarPalavras(arquivo)));
        }

        executor.shutdown();
        int totalPalavras = 0;

        for (Future<Integer> futuro : resultados) {
            try {
                totalPalavras += futuro.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Erro ao processar um dos arquivos.");
            }
        }

        System.out.println("Total de palavras: " + totalPalavras);
    }
}
