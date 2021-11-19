package br.com.infox.epp.processo.documento.download;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CarimboCancelamento implements Carimbo {

	private String textoCancelamento;
	@Setter
	private int absoluteX, absoluteY;
	private final int width, height;
	private final int posicao;

	public CarimboCancelamento(String textoCancelamento) {
		this(textoCancelamento, 0, 0, 0, 0, -1);
	}

	@Override
	public void aplicar(PdfContentByte content, Rectangle cropBox)
			throws MalformedURLException, IOException, DocumentException {
		Font f = new Font(Font.TIMES_ROMAN, 80);
		f.setColor(Color.RED);
		Phrase p = new Phrase(textoCancelamento, f);
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.5f);
		float x, y;
		x = (cropBox.getLeft() + cropBox.getRight()) / 2;
		y = (cropBox.getTop() + cropBox.getBottom()) / 2;
		content.saveState();
		content.setGState(gs1);
		ColumnText.showTextAligned(content, Element.ALIGN_CENTER, p, x, y, 45);
		content.restoreState();
	}

}