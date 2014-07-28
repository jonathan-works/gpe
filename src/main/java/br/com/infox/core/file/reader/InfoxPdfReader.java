package br.com.infox.core.file.reader;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

public class InfoxPdfReader {
    
    private static final LogProvider LOG = Logging.getLogProvider(InfoxPdfReader.class);
    
    private InfoxPdfReader(){
    }

    public static String readPdfFromInputStream(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            PdfReader reader = new PdfReader(inputStream);
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            int qtdPaginas = reader.getNumberOfPages();
            for (int i = 1; i <= qtdPaginas; i++) {
                sb.append(extractor.getTextFromPage(i));
            }
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar o conteúdo do pdf", e);
        }
        return sb.toString();
    }

}
