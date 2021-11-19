package br.com.infox.epp.processo.documento.download;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Getter @AllArgsConstructor
public class CarimboQrCode implements Carimbo {

	private String urlValidacaoDocumento;
	private UUID uuidDocumento;
	private String textoAssinatura;
	private boolean cancelado;

	@Setter
	private int absoluteX, absoluteY;
	private final int width, height;
	private int posicao = RODAPE;
	private boolean aplicarComoImagem;

	public CarimboQrCode(String urlValidacaoDocumento, UUID uuidDocumento, String textoAssinatura, boolean cancelado, int posicao) {
		this(urlValidacaoDocumento, uuidDocumento, textoAssinatura, cancelado, 0, 0, 250, 62, posicao, false);
	}

	private byte[] qrCode(int sideSize) {
		byte[] result = new byte[0];
    	String qrCodeContent = getUrlValidacaoDocumento();
		byte[] byteArray = QRCode.from(qrCodeContent).to(ImageType.PNG)
				.withSize(sideSize, sideSize).stream().toByteArray();
		try(ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			BufferedImage bufferedImage = ImageIO.read(bais);
			int cropSize = 10;
			BufferedImage subimage = bufferedImage.getSubimage(cropSize, cropSize, bufferedImage.getWidth()-(cropSize*2), bufferedImage.getHeight()-(cropSize*2));
			ImageIO.write(subimage, "png", baos);
			baos.flush();
			result = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Rectangle addQrCode(PdfContentByte content)
			throws BadElementException, MalformedURLException, IOException, DocumentException {
		float qrCodeSideSize = height-2;

		final Image image = Image.getInstance(qrCode( (int)qrCodeSideSize ));
		qrCodeSideSize = image.getWidth();

		float qrCodeLowerLeftX = 1;
		float qrCodeLowerLeftY = height - 1 - qrCodeSideSize;
		image.setAbsolutePosition(qrCodeLowerLeftX, qrCodeLowerLeftY);
		content.addImage(image);
		return new Rectangle(qrCodeLowerLeftX, qrCodeLowerLeftY, qrCodeLowerLeftX + qrCodeSideSize, qrCodeLowerLeftY + qrCodeSideSize);
	}

	@Override
	public void aplicar(PdfContentByte content, Rectangle cropBox) throws MalformedURLException, IOException, DocumentException {
		Rectangle qrCodeArea = addQrCode(content);

		float lowerLeftX = qrCodeArea.getRight();
		float lowerLeftY = 0;
		float upperRightX = width;
		float upperRightY = height;
		final Font font = new Font(Font.TIMES_ROMAN, 8);

		if (StringUtils.isNotBlank(textoAssinatura)) {
			float lowerHeight = height * 0.75f;
			ColumnText ct = new ColumnText(content);
			ct.setSimpleColumn(lowerLeftX, lowerLeftY + lowerHeight, upperRightX, upperRightY);
			upperRightY = lowerLeftY + lowerHeight;
			ct.setLeading(11);
			Phrase phrase = new Phrase(textoAssinatura, font);

			ct.addText(phrase);
			ct.addText(Chunk.NEWLINE);
			ct.go();
		}
		ColumnText ct = new ColumnText(content);
		ct.setSimpleColumn(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
		ct.setLeading(11);
		Phrase textoAutenticidade = new Phrase(resolveTextoAutenticidade(), font);
		String urlQRCode = getUrlValidacaoDocumento();
		Anchor codPhrase = new Anchor(urlQRCode, font);
		codPhrase.setReference(urlQRCode);
		textoAutenticidade.add(codPhrase);
		ct.addText(textoAutenticidade);
		ct.go();
	}

	private String resolveTextoAutenticidade() {
		String textoAutenticidade = TEXTO_AUTENTICIDADE_DOCUMENTO;
		if(cancelado) {
			textoAutenticidade = TEXTO_DOCUMENTO_CANCELADO + " " + TEXTO_AUTENTICIDADE_DOCUMENTO;
		}
		return textoAutenticidade;
	}

	private static final String TEXTO_DOCUMENTO_CANCELADO = "Documento cancelado.";
	private static final String TEXTO_AUTENTICIDADE_DOCUMENTO = "A autenticidade do documento pode ser conferida neste link: ";

}
