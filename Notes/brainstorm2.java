import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageMeanFilter {

    public static void applyMeanFilter(String inputPath, String outputPath, int kernelSize, int numThreads) throws IOException {
        // Load the image
        BufferedImage originalImage = ImageIO.read(new File(inputPath));
        BufferedImage filteredImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );

        // Divide the image into segments and create threads
        int height = originalImage.getHeight();
        int segmentHeight = height / numThreads;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int startRow = i * segmentHeight;
            int endRow = (i == numThreads - 1) ? height : startRow + segmentHeight;

            threads[i] = new Thread(() -> processSegment(originalImage, filteredImage, startRow, endRow, kernelSize));
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }

        // Save the filtered image
        ImageIO.write(filteredImage, "jpg", new File(outputPath));
    }

    private static void processSegment(BufferedImage originalImage, BufferedImage filteredImage, int startRow, int endRow, int kernelSize) {
        int width = originalImage.getWidth();

        for (int y = startRow; y < endRow; y++) {
            for (int x = 0; x < width; x++) {
                int[] avgColor = calculateNeighborhoodAverage(originalImage, x, y, kernelSize);
                synchronized (filteredImage) { // Ensure thread-safe writes to the image
                    filteredImage.setRGB(x, y,
                        (avgColor[0] << 16) |
                        (avgColor[1] << 8) |
                        avgColor[2]
                    );
                }
            }
        }
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

        return new int[]{
            (int) (redSum / pixelCount),
            (int) (greenSum / pixelCount),
            (int) (blueSum / pixelCount)
        };
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ImageMeanFilter <input_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        try {
            applyMeanFilter(inputFile, "filtered_output.jpg", 7, 4); // 4 threads
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}


Para aplicar a programação concorrente com threads dividindo a imagem e processando partes dela em paralelo, podemos criar várias threads, onde cada thread processa uma faixa da imagem diretamente. Vou adaptar a função de forma que ela processa partes da imagem em paralelo, manipulando diretamente os dados da imagem para aplicar o efeito no ruído.

Explicação
Divisão da Imagem:

A altura da imagem é dividida em numThreads segmentos.
Cada thread processa uma faixa específica da imagem.
Sincronização:

Para evitar condições de corrida durante a escrita na imagem final, usamos um bloco synchronized ao chamar filteredImage.setRGB.
Criação e Gerenciamento de Threads:

Threads são criadas e iniciadas para processar segmentos.
O método join é usado para esperar a conclusão de todas as threads antes de salvar a imagem.
Configuração Dinâmica:

O número de threads pode ser ajustado dinamicamente através do argumento numThreads.
Benefícios
Essa abordagem usa threads para processar segmentos da imagem simultaneamente, aproveitando múltiplos núcleos da CPU.
O código é seguro para concorrência devido ao uso de sincronização no momento da escrita na imagem final.





Você disse:
me diga quais foram as modificações em relação ao código original ?
O ChatGPT disse:
ChatGPT
As principais modificações em relação ao código original incluem:

1. Introdução da Programação Concorrente
Antes: O processamento da imagem era feito de forma sequencial, pixel a pixel, em um único loop.
Agora: A imagem é dividida em segmentos horizontais, e cada segmento é processado por uma thread separada.
2. Divisão da Imagem em Segmentos
Adicionamos lógica para dividir a altura da imagem proporcionalmente ao número de threads (numThreads).
Cada thread processa um intervalo de linhas, definido por startRow e endRow.
java
Copiar código
int segmentHeight = height / numThreads;
int startRow = i * segmentHeight;
int endRow = (i == numThreads - 1) ? height : startRow + segmentHeight;
3. Criação e Gerenciamento de Threads
Antes: Não havia uso de threads.
Agora:
Um array de threads é criado, e cada thread é responsável por processar um segmento da imagem.
As threads são iniciadas com o método start() e esperadas com join() para garantir que todas completem o processamento antes de salvar o arquivo.
java
Copiar código
Thread[] threads = new Thread[numThreads];
threads[i] = new Thread(() -> processSegment(originalImage, filteredImage, startRow, endRow, kernelSize));
threads[i].start();
4. Criação da Função processSegment
Uma nova função processSegment foi adicionada para encapsular o processamento de uma faixa de linhas da imagem.
Esta função é chamada pelas threads para aplicar o filtro nos segmentos atribuídos a elas.
java
Copiar código
private static void processSegment(BufferedImage originalImage, BufferedImage filteredImage, int startRow, int endRow, int kernelSize) {
    // Processa o segmento da imagem
}
5. Sincronização na Escrita na Imagem Final
Antes: O código escrevia os valores processados diretamente na imagem final sem preocupações com concorrência.
Agora: Um bloco synchronized foi adicionado ao chamar filteredImage.setRGB para evitar condições de corrida, pois múltiplas threads podem tentar escrever na imagem ao mesmo tempo.
java
Copiar código
synchronized (filteredImage) {
    filteredImage.setRGB(x, y, (avgColor[0] << 16) | (avgColor[1] << 8) | avgColor[2]);
}
6. Configuração Dinâmica do Número de Threads
Antes: Não havia conceito de threads.
Agora: O número de threads (numThreads) é um parâmetro da função applyMeanFilter, permitindo ajustar dinamicamente a concorrência conforme a capacidade do hardware.
java
Copiar código
applyMeanFilter(inputFile, "filtered_output.jpg", 7, 4); // 4 threads
7. Ajustes no Método main
Antes: O método main chamava a função original applyMeanFilter sem suporte a threads.
Agora: A função applyMeanFilter aceita o parâmetro numThreads para configurar o número de threads usadas no processamento.
