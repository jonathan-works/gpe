package br.com.infox.core.file.reader;

import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class InfoxPdfReader {
    
    private InfoxPdfReader(){
    }

    public static String readPdfFromInputStream(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            PdfReader reader = new PdfReader(inputStream);
            int qtdPaginas = reader.getNumberOfPages();
            for (int i = 1; i <= qtdPaginas; i++) {
                sb.append(PdfTextExtractor.getTextFromPage(reader, 1));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }

}
