package br.com.infox.epp.processo.documento.download;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;

import br.com.infox.epp.documento.type.LocalizacaoAssinaturaEletronicaDocumentoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @AllArgsConstructor
public class CarimboAssinatura implements Carimbo {

	private static final String TEXTO_RODAPE = "ASSINATURA ELETRÔNICA DOCUMENTAL";
	private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.0f);

	private byte[] imgAssinatura;
	private String nome, papel;
	@Setter
	private int absoluteX, absoluteY;
	private final int width, height;
	private int posicao = RODAPE;
	private LocalizacaoAssinaturaEletronicaDocumentoEnum localizacaoPaginas;
	private int paginaAplicacao;

	private static final int SIGNATURE_WIDTH=125, SIGNATURE_HEIGHT=62;

	public CarimboAssinatura(byte[] imgAssinatura, String nome, String papel, int orientacao, LocalizacaoAssinaturaEletronicaDocumentoEnum localizacaoPaginas, int paginaAplicacao) {
		this(imgAssinatura, nome, papel, 0,0,SIGNATURE_WIDTH,SIGNATURE_HEIGHT, orientacao, localizacaoPaginas, paginaAplicacao);
	}
	public CarimboAssinatura(byte[] imgAssinatura, String nome, String papel, LocalizacaoAssinaturaEletronicaDocumentoEnum localizacaoPaginas, int paginaAplicacao) {
		this(imgAssinatura, nome, papel, 0,0,SIGNATURE_WIDTH,SIGNATURE_HEIGHT, RODAPE, localizacaoPaginas, paginaAplicacao);
	}

    private int calcImagePartHeight(int h) {
        float height = h;
        height = height * 0.4f;
        return (int) height;
    }

	private BufferedImage resize(BufferedImage source,
            int width, int height) {
        return progressiveResize(source, width, height,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    private BufferedImage progressiveResize(BufferedImage source,
            int width, int height, Object hint) {
        int w = Math.max(source.getWidth()/2, width);
        int h = Math.max(source.getHeight()/2, height);
        BufferedImage img = commonResize(source, w, h, hint);
        while (w != width || h != height) {
            BufferedImage prev = img;
            w = Math.max(w/2, width);
            h = Math.max(h/2, height);
            img = commonResize(prev, w, h, hint);
            prev.flush();
        }
        return img;
    }

    private BufferedImage commonResize(BufferedImage source,
            int width, int height, Object hint) {
        BufferedImage img = new BufferedImage(width, height,
                source.getType());
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return img;
    }

    public BufferedImage getImage() {
		int w = this.width * 2;
    	int h = this.height * 2;
    	try {
	    	BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

	    	int iw = w-1;
	    	int ih = h-1;

	    	Graphics2D g = image.createGraphics();

	    	desenhaRetanguloExterno(iw, ih, g);
	    	int signImgHeight = calcImagePartHeight(h);
	    	desenhaBlocoAssinatura(iw, g, signImgHeight);
	    	desenhaImagemAssinatura(iw, g, signImgHeight);
	    	desenhaBlocoTexto(h, iw, g, signImgHeight);
	    	desenhaTexto(iw, g, signImgHeight);
	    	desenhaBlocoRodape(iw, ih, g);
	    	desenhaTextoRodape(iw, ih, g);
	    	g.dispose();
			image = resize(image, getWidth(), getHeight());
			return image;
    	} catch (NegativeArraySizeException e) {
    		throw e;
    	}
	}
	private void desenhaTextoRodape(int iw, int ih, Graphics2D g) {
		//texto do rodapé
		String textoRodape = TEXTO_RODAPE;
		int textoRodapeW = iw;
		int textoRodapeH = 15;
		int textoRodapeX = 3;
		int textoRodapeY = ih-15;
		g.setColor(Color.WHITE);
		drawWrappedTextPane(g, textoRodape, textoRodapeX, textoRodapeY, textoRodapeW, textoRodapeH, Font.PLAIN);
	}
	private void desenhaBlocoRodape(int iw, int ih, Graphics2D g) {
		//espaço do rodapé
		g.setColor(Color.BLACK);
		g.fillRect(3, ih-16, iw-5, 16);
	}
	private void desenhaTexto(int iw, Graphics2D g, int signImgHeight) {
		//texto do nome
		int nomeW = iw;
		int nomeH = 27;
		int nomeX = 3;
		int nomeY = (signImgHeight + 1) + 1;
		g.setColor(Color.BLACK);
		drawWrappedTextPane(g, nome, nomeX, nomeY, nomeW, nomeH, Font.BOLD);

		//texto do papel
		int papelW = iw;
		int papelH = 27;
		int papelX = 3;
		int papelY = nomeY + nomeH + 1;
		g.setColor(Color.BLACK);
		drawWrappedTextPane(g, papel, papelX, papelY, papelW, papelH, Font.PLAIN);
	}
	private void desenhaBlocoTexto(int h, int iw, Graphics2D g, int signImgHeight) {
		//espaço para texto
		g.setColor(Color.WHITE);
		g.fillRect(1, signImgHeight + 1, iw-1, h - signImgHeight - 2);
	}
	private void desenhaImagemAssinatura(int iw, Graphics2D g, int signImgHeight) {
		if(imgAssinatura != null) {
			try {
				g.setColor(Color.WHITE);
				BufferedImage icon = ImageIO.read(new ByteArrayInputStream(imgAssinatura));
				icon = resize(icon, iw-4, signImgHeight-2);

				g.drawImage(icon, null, 2, 2);
				icon.getGraphics().dispose();
			} catch (Exception e) {
			}
		}
	}
	private void desenhaBlocoAssinatura(int iw, Graphics2D g, int signImgHeight) {
		//espaço de imagem da assinatura
		g.setColor(Color.WHITE);
		g.fillRect(1, 1, iw-1, signImgHeight);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawRect(1, signImgHeight, iw-1, 1);
	}
	private void desenhaRetanguloExterno(int iw, int ih, Graphics2D g) {
		//desenha retângulo externo
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawRect(0, 0, iw, ih);
	}

    private static void drawWrappedTextPane(Graphics g, String text, int x, int y, int w, int h, int style) {
        JTextPane tp = new JTextPane();
        tp.setText(text);

        StyledDocument documentStyle = tp.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);
        tp.setBounds(0, 0, w, h);
        tp.setForeground(g.getColor());

        tp.setBackground(TRANSPARENT);
        tp.setFont(g.getFont().deriveFont(style, 10L));
        Graphics g2 = g.create(x, y, w, h);
        tp.paint(g2);
    }

	@Override
	public void aplicar(PdfContentByte content, Rectangle cropBox)
			throws MalformedURLException, IOException, DocumentException {
		BufferedImage bufferedImage = getImage();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
		Image image = Image.getInstance(baos.toByteArray());
		image.setAbsolutePosition(0, 0);
		content.addImage(image);
	}

	@Override
	public boolean podeAplicar(int page, int totalPaginas) {
		return LocalizacaoAssinaturaEletronicaDocumentoEnum.TODAS_PAGINAS.equals(localizacaoPaginas) || page == resolvePaginaAplicacao(totalPaginas);
	}
	private int resolvePaginaAplicacao(int totalPaginas) {
		int paginaAplicacao = this.paginaAplicacao;
        switch (localizacaoPaginas) {
            case PAGINA_UNICA:
                paginaAplicacao = Math.min(totalPaginas, Math.max(paginaAplicacao, 1));
                break;
            case PRIMEIRA_PAGINA:
            	paginaAplicacao = 1;
                break;
            case ULTIMA_PAGINA:
            default:
            	paginaAplicacao = totalPaginas;
                break;
        }
		return paginaAplicacao;
	}

}