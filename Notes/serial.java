import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReducaoDeRuido {

    // Função que aplica o efeito de redução de ruído
    private static void aplicarReducaoDeRuido(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // Processa toda a imagem sequencialmente (sem concorrência)
        for (int y = 1; y < altura - 1; y++) {
            for (int x = 1; x < largura - 1; x++) {
                Color corMedia = calcularCorMedia(imagem, x, y);
                imagem.setRGB(x, y, corMedia.getRGB());
            }
        }
    }

    // Função que calcula a cor média ao redor de um pixel (para redução de ruído)
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

        // Aplica a redução de ruído de forma sequencial
        aplicarReducaoDeRuido(imagem);

        // Salva a imagem processada
        File arquivoSaida = new File("imagem_reduzida.png");
        ImageIO.write(imagem, "PNG", arquivoSaida);
    }
}
