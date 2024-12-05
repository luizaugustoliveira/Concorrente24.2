Claro! Aqui estão apenas as modificações que diferem do código original:

### 1. Divisão das linhas da imagem entre as threads:
Modifiquei a lógica para dividir a imagem em blocos de linhas para serem processadas por diferentes threads.

```java
// Número de linhas por thread
int height = originalImage.getHeight();
int linesPerThread = height / numThreads;
```

### 2. Criação manual das threads:
Criei as threads manualmente e dividi a responsabilidade do processamento das linhas entre elas.

```java
// Criar e iniciar as threads
Thread[] threads = new Thread[numThreads];

for (int i = 0; i < numThreads; i++) {
    final int threadIndex = i;
    threads[i] = new Thread(() -> {
        int startY = threadIndex * linesPerThread;
        int endY = (threadIndex == numThreads - 1) ? height : (threadIndex + 1) * linesPerThread;
        
        // Processar as linhas atribuídas a essa thread
        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, kernelSize);
                
                // Definir o pixel filtrado
                filteredImage.setRGB(x, y, 
                    (avgColor[0] << 16) | 
                    (avgColor[1] << 8)  | 
                    avgColor[2]
                );
            }
        }
    });
    threads[i].start();  // Iniciar a thread
}
```

### 3. Sincronização das threads:
Aguardei todas as threads terminarem utilizando o método `join()`.

```java
// Esperar todas as threads terminarem
for (Thread thread : threads) {
    try {
        thread.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

Essas são as modificações chave para implementar a concorrência usando threads manuais.

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Esta classe aplica um filtro de média em uma imagem com concorrência.
 * O filtro de média é usado para suavizar imagens, fazendo a média dos valores dos pixels
 * em uma vizinhança definida por um tamanho de kernel.
 */
public class ImageMeanFilterConcurrent {

    /**
     * Aplica o filtro de média em uma imagem de forma concorrente.
     * 
     * @param inputPath  Caminho da imagem de entrada
     * @param outputPath Caminho da imagem de saída
     * @param kernelSize Tamanho do kernel do filtro de média
     * @param numThreads Número de threads a serem utilizadas
     * @throws IOException Se houver erro ao ler ou salvar a imagem
     */
    public static void applyMeanFilter(String inputPath, String outputPath, int kernelSize, int numThreads) throws IOException {
        // Carregar a imagem original
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        
        // Criar a imagem resultante
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Número de linhas por thread
        int height = originalImage.getHeight();
        int linesPerThread = height / numThreads;
        
        // Criar e iniciar as threads
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int startY = threadIndex * linesPerThread;
                int endY = (threadIndex == numThreads - 1) ? height : (threadIndex + 1) * linesPerThread;
                
                // Processar as linhas atribuídas a essa thread
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < originalImage.getWidth(); x++) {
                        int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, kernelSize);
                        
                        // Definir o pixel filtrado
                        filteredImage.setRGB(x, y, 
                            (avgColor[0] << 16) | 
                            (avgColor[1] << 8)  | 
                            avgColor[2]
                        );
                    }
                }
            });
            threads[i].start();  // Iniciar a thread
        }
        
        // Esperar todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // Salvar a imagem filtrada
        ImageIO.write(filteredImage, "jpg", new File(outputPath));
    }

    /**
     * Calcula a média das cores de uma vizinhança em torno de um pixel.
     * 
     * @param image      A imagem de origem
     * @param centerX    A coordenada X do pixel central
     * @param centerY    A coordenada Y do pixel central
     * @param kernelSize O tamanho do kernel (deve ser ímpar)
     * @return Um array contendo as médias de R, G e B
     */
    private static int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        // Arrays para somar as cores
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        // Processar a vizinhança
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                // Verificar limites da imagem
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    // Obter a cor do pixel
                    int rgb = image.getRGB(x, y);
                    
                    // Extrair os componentes de cor
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    // Somar as cores
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }
        
        // Calcular a média
        return new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };
    }

    /**
     * Método principal para demonstração
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java ImageMeanFilterConcurrent <input_file> <num_threads>");
            System.exit(1);
        }

        String inputFile = args[0];
        int numThreads = Integer.parseInt(args[1]);

        try {
            applyMeanFilter(inputFile, "filtered_output.jpg", 3, numThreads);
        } catch (IOException e) {
            System.err.println("Erro ao processar a imagem: " + e.getMessage());
        }
    }
}

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.*;

public class ImageMeanFilterConcurrent {
    
    public static void applyMeanFilter(String inputPath, String outputPath, int kernelSize, int numThreads) throws IOException, InterruptedException, ExecutionException {
        // Load image
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        
        // Create result image
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );

        // Initialize ExecutorService for concurrency
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int rowsPerThread = height / numThreads;
        
        // List to hold futures of tasks
        List<Future<Void>> futures = new ArrayList<>();
        
        // Divide the image processing into rows
        for (int i = 0; i < numThreads; i++) {
            final int startRow = i * rowsPerThread;
            final int endRow = (i == numThreads - 1) ? height : (i + 1) * rowsPerThread;
            
            // Submit a task for processing each block of rows
            futures.add(executor.submit(() -> {
                for (int y = startRow; y < endRow; y++) {
                    for (int x = 0; x < width; x++) {
                        int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, kernelSize);
                        filteredImage.setRGB(x, y, (avgColor[0] << 16) | (avgColor[1] << 8) | avgColor[2]);
                    }
                }
                return null; // Return null as we don't need any result from the task
            }));
        }
        
        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            future.get(); // This blocks until the task is finished
        }
        
        // Save the filtered image
        ImageIO.write(filteredImage, "jpg", new File(outputPath));
        
        // Shutdown the executor service
        executor.shutdown();
    }

    private static int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }
        
        return new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java ImageMeanFilterConcurrent <input_file> <num_threads>");
            System.exit(1);
        }

        String inputFile = args[0];
        int numThreads = Integer.parseInt(args[1]);
        
        try {
            applyMeanFilter(inputFile, "filtered_output_concurrent.jpg", 3, numThreads);
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class provides functionality to apply a mean filter to an image
 * with concurrency, by dividing the image into quadrants and processing
 * each part in a separate thread.
 */
public class ImageMeanFilter {

    /**
     * Applies mean filter to an image using multiple threads
     * 
     * @param inputPath  Path to input image
     * @param outputPath Path to output image 
     * @param kernelSize Size of mean kernel
     * @param numThreads Number of threads to use
     * @throws IOException If there is an error reading/writing
     */
    public static void applyMeanFilterWithConcurrency(String inputPath, String outputPath, int kernelSize, int numThreads) throws IOException {
        // Load image
        BufferedImage originalImage = ImageIO.read(new File(inputPath));

        // Get image dimensions
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create result image
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Divide image into regions for each thread
        int rowsPerThread = height / numThreads;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int startY = i * rowsPerThread;
            final int endY = (i == numThreads - 1) ? height : (i + 1) * rowsPerThread;

            // Create and start each thread
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    processRegion(originalImage, filteredImage, startY, endY, kernelSize);
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Save filtered image
        ImageIO.write(filteredImage, "jpg", new File(outputPath));
    }

    /**
     * Processes a region of the image applying the mean filter
     * 
     * @param image       Source image
     * @param filteredImage The result image where filtered pixels are set
     * @param startY      Starting Y coordinate of the region
     * @param endY        Ending Y coordinate of the region
     * @param kernelSize  Kernel size
     */
    private static void processRegion(BufferedImage image, BufferedImage filteredImage, int startY, int endY, int kernelSize) {
        int width = image.getWidth();

        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < width; x++) {
                int[] avgColor = calculateNeighborhoodAverage(image, x, y, kernelSize);
                filteredImage.setRGB(x, y, (avgColor[0] << 16) | (avgColor[1] << 8) | avgColor[2]);
            }
        }
    }

    /**
     * Calculates average colors in a pixel's neighborhood
     * 
     * @param image      Source image
     * @param centerX    X coordinate of center pixel
     * @param centerY    Y coordinate of center pixel
     * @param kernelSize Kernel size
     * @return Array with R, G, B averages
     */
    private static int[] calculateNeighborhoodAverage(BufferedImage image, int centerX, int centerY, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pad = kernelSize / 2;
        
        // Arrays for color sums
        long redSum = 0, greenSum = 0, blueSum = 0;
        int pixelCount = 0;
        
        // Process neighborhood
        for (int dy = -pad; dy <= pad; dy++) {
            for (int dx = -pad; dx <= pad; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;
                
                // Check image bounds
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    // Get pixel color
                    int rgb = image.getRGB(x, y);
                    
                    // Extract color components
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    // Sum colors
                    redSum += red;
                    greenSum += green;
                    blueSum += blue;
                    pixelCount++;
                }
            }
        }
        
        // Calculate average
        return new int[] {
            (int)(redSum / pixelCount),
            (int)(greenSum / pixelCount),
            (int)(blueSum / pixelCount)
        };
    }

    /**
     * Main method for demonstration
     * 
     * Usage: java ImageMeanFilter <input_file> <num_threads>
     * 
     * Arguments:
     *   input_file - Path to the input image file to be processed
     *   num_threads - Number of threads to use for processing
     * 
     * Example:
     *   java ImageMeanFilter input.jpg 4
     * 
     * The program will generate a filtered output image named "filtered_output.jpg"
     * using a 7x7 mean filter kernel and 4 threads for parallel processing.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java ImageMeanFilter <input_file> <num_threads>");
            System.exit(1);
        }

        String inputFile = args[0];
        int numThreads = Integer.parseInt(args[1]);
        
        try {
            applyMeanFilterWithConcurrency(inputFile, "filtered_output.jpg", 7, numThreads);
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}
