package br.gov.mt.cuiaba.pmc.gdprev;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
import com.lowagie.text.pdf.PdfSmartCopy;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.epp.access.api.RolesMap;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.login.LoginService;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

@Stateless
public class ProcessoEndpointService {

    public static final String RECURSO_ACESSO_WS = "acessaWSProcessoSoap";

    @Inject
    private ProcessoEndpointSearch processoEndpointSearch;
    @Inject
    private PdfManager pdfManager;
    @Inject
    private FileDownloader fileDownloader;
    @Inject
    private LoginService loginService;
    @Inject
    private AuthenticatorService authenticatorService;
    @Inject
    private UsuarioPerfilDAO usuarioPerfilDAO;
    @Inject
    private DocumentoBinManager documentoBinManager;

    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;

    @Inject
    private ProcessoManager processoManager;


    public byte[] gerarPDFProcesso(ProcessoDTO processoDTO) {
        return gerarPDFProcesso(pdfManager, fileDownloader, processoDTO);
    }

    private byte[] gerarPDFProcesso(PdfManager pdfManager, FileDownloader fileDownloader, ProcessoDTO processoDTO) {
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        try {
            com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document();
            // PdfCopy copy = new PdfCopy(pdfDocument, pdf);
            PdfSmartCopy smaretCopy = new PdfSmartCopy(pdfDocument, pdf);
            pdfDocument.open();

            Processo processo = processoManager.find(processoDTO.getId());

            if (processo != null) {

                pdfManager.copySmartPdf(smaretCopy, fileDownloader.getData(processo.getDocumentoBinResumoProcesso(), false));

            } else {
                DocumentoBin documentoBinResumoDocumentosProcesso = documentoBinManager.createDocumentoBinResumoDocumentosProcesso(processo);

                pdfManager.copySmartPdf(smaretCopy, fileDownloader.getData(documentoBinResumoDocumentosProcesso, false));
            }


            pdfDocument.addTitle("numeroDoProcesso");
            pdfDocument.close();
        } catch (DocumentException |
                 IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Erro", e);
        }
        return pdf.toByteArray();
    }

    public void autenticar(String username, String password) {
        if (loginService.autenticar(username, password)) {
            if (!podeAcessarWSSoap(username)) {
                throw new WebServiceException(Status.FORBIDDEN.getStatusCode(), "HTTP" + Status.FORBIDDEN.getStatusCode(), "Recurso não disponível");
            }
        } else {
            throw new WebServiceException(Status.UNAUTHORIZED.getStatusCode(), "HTTP" + Status.UNAUTHORIZED.getStatusCode(), "Não autorizado");
        }
    }

    private static final String RECURSO = "acessaWSProcessoSoap";

    private boolean podeAcessarWSSoap(String username) {
        authenticatorService.autenticaManualmenteNoSeamSecurity(username, IdentityManager.instance());
        Set<String> roleSet = new HashSet<>();
        for (UsuarioPerfil usuarioPerfil : usuarioPerfilDAO.listByLogin(username)) {
            roleSet.addAll(RolesMap.instance().getChildrenRoles(usuarioPerfil.getPerfilTemplate().getPapel().getIdentificador()));
        }
        for (String role : roleSet) {
            Identity.instance().addRole(role);
        }
        return Identity.instance().hasPermission(RECURSO, "access");
    }

    private String resolverModeloComContexto(Integer idProcesso, String codigoModelo) {
        return resolverModeloComContexto(idProcesso, codigoModelo, null);
    }

    private String resolverModeloComContexto(Integer idProcesso, String codigoModelo, Object contexto) {
        return modeloDocumentoManager.resolverModeloComContexto(idProcesso, codigoModelo, contexto);
    }

}
