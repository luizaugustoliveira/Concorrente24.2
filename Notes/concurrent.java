import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReducaoDeRuidoConcorrente {

    // Função que aplica o efeito de redução de ruído
    private static void aplicarReducaoDeRuido(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // Criar um array para armazenar as threads
        Thread[] threads = new Thread[4];  // 4 threads, para 4 quadrantes

        // Divide a imagem em quadrantes e cria uma thread para cada quadrante
        for (int y = 0; y < altura; y += altura / 2) {
            for (int x = 0; x < largura; x += largura / 2) {
                final int startX = x;
                final int startY = y;
                threads[(y / (altura / 2)) * 2 + (x / (largura / 2))] = new Thread(() -> {
                    processarQuadrante(imagem, startX, startY, largura / 2, altura / 2);
                });
            }
        }

        // Iniciar todas as threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Aguardar a conclusão de todas as threads
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Função que processa um quadrante da imagem
    private static void processarQuadrante(BufferedImage imagem, int xOffset, int yOffset, int largura, int altura) {
        for (int y = yOffset + 1; y < yOffset + altura - 1; y++) {
            for (int x = xOffset + 1; x < xOffset + largura - 1; x++) {
                Color corMedia = calcularCorMedia(imagem, x, y);
                imagem.setRGB(x, y, corMedia.getRGB());
            }
        }
    }

    // Função que calcula a cor média ao redor de um pixel
    private static Color calcularCorMedia(BufferedImage imagem, int x, int y) {
        int somaR = 0, somaG = 0, somaB = 0;
        int numPixels = 0;

        // Percorre os 8 vizinhos do pixel
        for (int j = y - 1; j <= y + 1; j++) {
            for (int i = x - 1; i <= x + 1; i++) {
                if (i >= 0 && j >= 0 && i < imagem.getWidth() && j < imagem.getHeight()) {
                    Color cor = new Color(imagem.getRGB(i, j));
                    somaR += cor.getRed();
                    somaG += cor.getGreen();
                    somaB += cor.getBlue();
                    numPixels++;
                }
            }
        }

        int mediaR = somaR / numPixels;
        int mediaG = somaG / numPixels;
        int mediaB = somaB / numPixels;

        return new Color(mediaR, mediaG, mediaB);
    }

    public static void main(String[] args) throws IOException {
        File arquivo = new File("imagem.png");
        BufferedImage imagem = ImageIO.read(arquivo);

        // Aplica a redução de ruído de forma concorrente
        aplicarReducaoDeRuido(imagem);

        // Salva a imagem processada
        File arquivoSaida = new File("imagem_reduzida_concorrente.png");
        ImageIO.write(imagem, "PNG", arquivoSaida);
    }
}
