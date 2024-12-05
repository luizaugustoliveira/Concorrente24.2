import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class ReducaoDeRuidoSerial {

    public static void main(String[] args) throws IOException {
        // Carregar a imagem
        BufferedImage imagem = carregarImagem("imagem.jpg");

        // Obter a largura e altura da imagem
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        // Aplica a redução de ruído
        long start = System.currentTimeMillis();
        BufferedImage imagemProcessada = reduzirRuido(imagem, largura, altura);
        long end = System.currentTimeMillis();

        // Salvar a imagem resultante
        salvarImagem(imagemProcessada, "imagem_reduzida_serial.jpg");

        System.out.println("Tempo de execução serial: " + (end - start) + "ms");
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

    // Função para redução de ruído de forma sequencial
    private static BufferedImage reduzirRuido(BufferedImage imagem, int largura, int altura) {
        BufferedImage imagemProcessada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);

        // Aplica a redução de ruído em toda a imagem de forma sequencial
        for (int y = 1; y < altura - 1; y++) {
            for (int x = 1; x < largura - 1; x++) {
                // Aplica a média dos pixels vizinhos (um filtro 3x3)
                Color corMedia = calcularCorMedia(imagem, x, y);
                imagemProcessada.setRGB(x, y, corMedia.getRGB());
            }
        }

        return imagemProcessada;
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


///
/// ### Explicação da Versão Serial


/// 1. **carregarImagem**: Carrega a imagem de um arquivo.

/// 2. **reduzirRuido**: Aplica a redução de ruído (filtro 3x3) pixel por pixel na imagem.

/// 3. **calcularCorMedia**: Calcula a média dos valores de cor dos pixels vizinhos de um pixel específico.

/// 4. **salvarImagem**: Salva a imagem processada em um novo arquivo.
/// 
/// 