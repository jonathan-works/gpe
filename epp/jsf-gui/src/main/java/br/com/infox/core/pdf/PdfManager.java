package br.com.infox.core.pdf;

import java.io.IOException;
import java.io.OutputStream;

import javax.ejb.Stateless;

import com.lowagie.text.pdf.PdfSmartCopy;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

@Stateless
@Name(PdfManager.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class PdfManager {
    
    public static final String NAME = "pdfManager";
	
	public void convertHtmlToPdf(String html, OutputStream out) throws DocumentException {
	    W3CDom w3cDom = new W3CDom();
		Document doc = Jsoup.parse(html);
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		moveStylesToHead(doc);
		Element head = doc.head();
		Element style = new Element(Tag.valueOf("style"), doc.baseUri());
		style.text("img { -fs-fit-images-to-width: 100% }");
		head.appendChild(style);
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(w3cDom.fromJsoup(doc), doc.baseUri());
		renderer.layout();
		renderer.createPDF(out);
	}

	private void moveStylesToHead(Document doc) {
	    Elements styles = doc.select("style");
	    Element head = doc.head();
	    for (Element style : styles) {
	        style.remove();
            head.appendChild(style);
        }
    }

    public PdfCopy copyPdf(PdfCopy copy, byte[] pdf) throws IOException, BadPdfFormatException {
		PdfReader reader = new PdfReader(pdf);
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			copy.addPage(copy.getImportedPage(reader, i));
		}
		copy.freeReader(reader);
		reader.close();
		return copy;
	}

	public PdfSmartCopy copySmartPdf(PdfSmartCopy copy, byte[] pdf) throws IOException, BadPdfFormatException {
		PdfReader reader = new PdfReader(pdf);
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			copy.addPage(copy.getImportedPage(reader, i));
		}
		copy.freeReader(reader);
		reader.close();
		return copy;
	}
}
