package br.com.infox.epp.processo.documento.download;

import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;

public interface Carimbo {

	int RODAPE = 1, LATERAL = 2;

	int getAbsoluteX();
	int getAbsoluteY();

	void setAbsoluteX(int x);
	void setAbsoluteY(int y);

	int getWidth();
	int getHeight();

	// RODAPÉ = 1, LATERAL = 2. PADRÃO 1
	int getPosicao();

	default boolean podeAplicar(int pagina, int totalPaginas) {
		return true;
	}

	void aplicar(PdfContentByte content, Rectangle cropBox) throws MalformedURLException, IOException, DocumentException;

	default int getUpperRightX() {
		int urx = getAbsoluteX() + getWidth();
		if (LATERAL == getPosicao()) {
			urx = getAbsoluteX() - getHeight();
		}
		return urx;
	}
	default int getUpperRightY() {
		int ury = getAbsoluteY() + getHeight();
		if (LATERAL == getPosicao()) {
			ury = getAbsoluteY() - getWidth();
		}
		return ury;
	}

}