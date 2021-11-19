package br.com.infox.epp.processo.documento.download;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import lombok.Getter;

@Getter
public class Carimbador {
	private final List<Carimbo> carimbosRodape;
	private final List<Carimbo> carimbosLateral;
	private final List<Carimbo> carimbosAbsolutos;

	public Carimbador(List<Carimbo> carimbos) {
		final List<Carimbo> carimbosRodape = new ArrayList<>();
		final List<Carimbo> carimbosLateral = new ArrayList<>();
		final List<Carimbo> carimbosAbsolutos = new ArrayList<>();
		for (Carimbo carimbo : carimbos) {
			switch (carimbo.getPosicao()) {
			case Carimbo.RODAPE:
				carimbosRodape.add(carimbo);
				break;
			case Carimbo.LATERAL:
				carimbosLateral.add(carimbo);
				break;
			default:
				carimbosAbsolutos.add(carimbo);
				break;
			}
		}
		this.carimbosRodape = Collections.unmodifiableList(carimbosRodape);
		this.carimbosLateral = Collections.unmodifiableList(carimbosLateral);
		this.carimbosAbsolutos = Collections.unmodifiableList(carimbosAbsolutos);
	}

	public List<Carimbo> getCarimbos(){
		ArrayList<Carimbo> arrayList = new ArrayList<>();
		arrayList.addAll(getCarimbosRodape());
		arrayList.addAll(getCarimbosLateral());
		arrayList.addAll(getCarimbosAbsolutos());
		return arrayList;
	}

	private static final int BOUNDARY_STEP = 5;
	private static final int ELEMENT_STEP = 5;

	private void calcularPosicoesRodape(Rectangle cropBox, Predicate<Carimbo> filter) {
		int absoluteX = BOUNDARY_STEP;
		int absoluteY = BOUNDARY_STEP;

		for (Carimbo carimbo : carimbosRodape) {
			if (filter.test(carimbo)) {
				int width = carimbo.getWidth();
				carimbo.setAbsoluteX(absoluteX);
				carimbo.setAbsoluteY(absoluteY);
				absoluteX += (width + ELEMENT_STEP);
			}
		}
	}
	private void calcularPosicoesLateral(Rectangle cropBox, Predicate<Carimbo> filter) {
		int absoluteX = (int) (cropBox.getRight() - BOUNDARY_STEP);
		int absoluteY = (int) (cropBox.getTop() - BOUNDARY_STEP);

		for (Carimbo carimbo : carimbosLateral) {
			if (filter.test(carimbo)) {
				int width = carimbo.getWidth();
				carimbo.setAbsoluteX(absoluteX - carimbo.getHeight());
				carimbo.setAbsoluteY(absoluteY);
				absoluteY -= (width + ELEMENT_STEP);
			}
		}
	}

	public void calcularPosicoes(Rectangle cropBox, Predicate<Carimbo> filter) {
		calcularPosicoesLateral(cropBox, filter);
		calcularPosicoesRodape(cropBox, filter);
	}

	private void aplicarCarimbo(Carimbo carimbo, PdfContentByte content, int rotation, Rectangle cropBox) throws IOException, DocumentException {
		double alpha = rotation * Math.PI / 180.0;
		float cos = (float)Math.cos(alpha);
		float sin = (float)Math.sin(alpha);
		content.saveState();
		content.concatCTM(cos, -sin, sin, cos, carimbo.getAbsoluteX(), carimbo.getAbsoluteY());
		carimbo.aplicar(content, cropBox);
		content.restoreState();
	}

	public void aplicar(PdfContentByte content, int page, int numberOfPages, int _rotation, Rectangle cropBox) throws DocumentException, IOException {
		Predicate<Carimbo> isVisivel = carimbo-> carimbo.podeAplicar(page, numberOfPages);
		calcularPosicoes(cropBox, isVisivel);

		for (Carimbo carimbo : carimbosRodape) {
			if (isVisivel.test(carimbo)) {
				aplicarCarimbo(carimbo, content, 0, cropBox);
			}
		}
		for (Carimbo carimbo : carimbosLateral) {
			if (isVisivel.test(carimbo)) {
				aplicarCarimbo(carimbo, content, 90, cropBox);
			}
		}
		for (Carimbo carimbo : carimbosAbsolutos) {
			if (isVisivel.test(carimbo)) {
				carimbo.aplicar(content, cropBox);
			}
		}
	}

	public void aplicar(PdfReader pdfReader, PdfStamper stamper) throws DocumentException, IOException {
		int numberOfPages = pdfReader.getNumberOfPages();
		for (int page = 1; page <= numberOfPages; page++) {
    		final PdfContentByte content = stamper.getOverContent(page);
    		int rotation = pdfReader.getPageRotation(page);
    		final Rectangle cropBox = pdfReader.getCropBox(page);
    		aplicar(content, page, numberOfPages, rotation, cropBox);
		}
	}


}