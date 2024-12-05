import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.concurrent.*;

public class ReducaoDeRuidoConcorrente {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Carregar a imagem
        BufferedImage imagem = carregarImagem("imagem.jpg");

        // Obter a largura e altura da imagem
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // Executor para gerenciar as threads
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Void>> futures = new ArrayList<>();

        // Dividir a imagem em quadrantes e processar cada um com uma thread
        for (int y = 0; y < altura; y += altura / 2) {
            for (int x = 0; x < largura; x += largura / 2) {
                final int startX = x;
                final int startY = y;
                futures.add(executor.submit(() -> {
                    processarQuadrante(imagem, startX, startY, largura / 2, altura / 2);
                    return null; // Nenhum valor retornado, apenas processamento
                }));
            }
        }

        // Aguardar a conclusão das tarefas
        for (Future<Void> future : futures) {
            future.get();
        }

        // Fechar o executor
        executor.shutdown();

        // Salvar a imagem processada
        salvarImagem(imagem, "imagem_reduzida_concorrente.jpg");
    }

    // Carregar a imagem do arquivo
    private static BufferedImage carregarImagem(String caminho) throws IOException {
        File arquivo = new File(caminho);
        return ImageIO.read(arquivo);
    }

    // Salvar a imagem no arquivo
    private static void salvarImagem(BufferedImage imagem, String caminho) throws IOException {
        File arquivo = new File(caminho);
        ImageIO.write(imagem, "jpg", arquivo);
    }

    // Função para processar um quadrante da imagem (aplica a redução de ruído)
    private static void processarQuadrante(BufferedImage imagem, int xOffset, int yOffset, int largura, int altura) {
        for (int y = yOffset + 1; y < yOffset + altura - 1; y++) {
            for (int x = xOffset + 1; x < xOffset + largura - 1; x++) {
                Color corMedia = calcularCorMedia(imagem, x, y);
                imagem.setRGB(x, y, corMedia.getRGB());
            }
        }
    }

    // Função que calcula a cor média dos pixels vizinhos (filtro 3x3)
    private static Color calcularCorMedia(BufferedImage imagem, int x, int y) {
        int somaR = 0, somaG = 0, somaB = 0;
        int count = 0;

        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int corPixel = imagem.getRGB(x + i, y + j);
                Color cor = new Color(corPixel);
                somaR += cor.getRed();
                somaG += cor.getGreen();
                somaB += cor.getBlue();
                count++;
            }
        }

        // Calcular a média
        int mediaR = somaR / count;
        int mediaG = somaG / count;
        int mediaB = somaB / count;

        return new Color(mediaR, mediaG, mediaB);
    }
}

//*
### Explicação da Versão Concorrente

1. **ExecutorService**: Usamos um pool de threads com `Executors.newFixedThreadPool(4)` para dividir o trabalho entre 4 threads.
2. **processarQuadrante**: Para cada quadrante da imagem, uma thread é responsável por aplicar a redução de ruído.
3. **Divisão da imagem**: A imagem é dividida em quadrantes, e a função `processarQuadrante` é chamada para cada um desses quadrantes.

### Resumo:

- **Versão Serial**: Processa cada pixel da imagem sequencialmente, sem paralelismo.
- **Versão Concorrente**: Divide a imagem em quadrantes e processa cada quadrante em uma thread separada, melhorando a performance em máquinas com múltiplos núcleos.

Esse código é mais simples e direto, focando apenas na divisão básica da imagem e no uso de threads para concorrência.


///