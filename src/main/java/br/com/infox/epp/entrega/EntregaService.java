package br.com.infox.epp.entrega;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.lowagie.text.DocumentException;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

@Stateless
public class EntregaService {
    public static final String PARAMETRO_ENTREGA_ENTREGA = "entrega";

	@Inject
	private ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private PdfManager pdfManager;
	
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManagerBin();
    }
    
	/**
	 * Gera um arquivo PDF que representa a certidão de entrega
	 */
	public byte[] gerarPdfCertidao(Entrega entrega, Map<String, String> mapaEL) {
		ModeloEntrega modeloEntrega = entrega.getModeloEntrega();
		ModeloDocumento modeloCertidao = modeloEntrega.getModeloCertidao();
		
		ArbitraryExpressionResolver resolver = new ArbitraryExpressionResolver(mapaEL);
		String documento = modeloDocumentoManager.evaluateModeloDocumento(modeloCertidao, resolver);
		
		ByteArrayOutputStream pdf = new ByteArrayOutputStream();
		try {
			pdfManager.convertHtmlToPdf(documento, pdf);
			return pdf.toByteArray();
		} catch (DocumentException e) {
			throw new RuntimeException("Erro ao gerar certidão de entrega", e);
		}		
	}

	/**
	 * Gera uma certidão para uma entrega e grava no banco de dados
	 */
	public void gerarCertidao(Entrega entrega, Map<String, String> mapaEL) {
		byte[] pdf = gerarPdfCertidao(entrega, mapaEL);
		DocumentoBin documentoBin = documentoBinManager.createProcessoDocumentoBin("Certidão Entrega.pdf", pdf, "pdf");
		documentoBin = documentoBinManager.createProcessoDocumentoBin(documentoBin);
		getEntityManager().flush();
		entrega.setCertidaoEntrega(documentoBin);
	}

}
