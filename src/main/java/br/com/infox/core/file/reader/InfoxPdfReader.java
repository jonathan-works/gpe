package br.com.infox.core.file.reader;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class InfoxPdfReader {
    
    private static final LogProvider LOG = Logging.getLogProvider(InfoxPdfReader.class);
    
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
            LOG.error("Não foi possível recuperar o conteúdo do pdf", e);
        }
        return sb.toString();
    }

}
