package br.com.infox.jsf.workaround;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jboss.seam.pdf.ui.UIDocument;

import com.lowagie.text.DocWriter;

import com.lowagie.text.Document;

import com.lowagie.text.DocumentException;

import com.lowagie.text.ExceptionConverter;

import com.lowagie.text.Rectangle;

import com.lowagie.text.pdf.BaseFont;

import com.lowagie.text.pdf.PdfContentByte;

import com.lowagie.text.pdf.PdfPageEventHelper;

import com.lowagie.text.pdf.PdfTemplate;

import com.lowagie.text.pdf.PdfWriter;

public class SeamPdfDocument extends UIDocument {

    private PdfTemplate templateTotalPaginas;

    private BaseFont fonte;

    /** {@inheritDoc} */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        DocWriter writer = getWriter();
        if (writer != null && writer instanceof PdfWriter) {
            PdfWriter pdfWriter = (PdfWriter) writer;
            pdfWriter.setPageEvent(new PdfDocumentEventListener());
            templateTotalPaginas = pdfWriter.getDirectContent().createTemplate(100, 100);
            templateTotalPaginas.setBoundingBox(new Rectangle(-20, -20, 100, 100));
            try {
                fonte = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            } catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    private class PdfDocumentEventListener extends PdfPageEventHelper {
    	
        /** {@inheritDoc} */
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            String text = writer.getPageNumber() + " de ";
            float textSize = fonte.getWidthPoint(text, 10);
            float textBase = document.bottom() - 20;
            float meio = (document.left() + document.right()) / 2;
            cb.beginText();
            cb.setFontAndSize(fonte, 10);
            cb.setTextMatrix(meio - textSize, textBase);
            cb.showText(text);
            cb.endText();
            cb.addTemplate(templateTotalPaginas, meio, textBase);
            cb.restoreState();
        }

		/** {@inheritDoc} */
		@Override
		public void onCloseDocument(PdfWriter writer, Document document) {
			templateTotalPaginas.beginText();
			templateTotalPaginas.setFontAndSize(fonte, 10);
			templateTotalPaginas.setTextMatrix(0, 0);
			templateTotalPaginas.showText(Integer.toString(writer.getPageNumber() - 1));
			templateTotalPaginas.endText();
		}

    }

}

