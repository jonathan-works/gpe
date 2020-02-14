package br.gov.mt.cuiaba.pmc.gdprev;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.Holder;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
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
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.login.LoginService;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;

@Stateless
public class ProcessoEndpointService {

    public static final String RECURSO_ACESSO_WS = "acessaWSProcessoSoap";

    private <T> void with(Class<T> type, Consumer<T> consumer) {
        T service = null;
        try {
            Lifecycle.beginCall();
            service = Beans.getReference(type);
            consumer.accept(service);
        } finally {
            if (service != null) {
                Beans.destroy(service);
            }
            if (Contexts.isSessionContextActive()) {
                Lifecycle.endCall();
            }
        }
    }

    public byte[] gerarPDFProcesso(String numeroDoProcesso, List<DocumentoBin> documentos) {
        Holder<byte[]> dataHolder = new Holder<>();
        with(PdfManager.class, pdfManager->
            with(FileDownloader.class, fileDownloader->
                dataHolder.value = gerarPDFProcesso(pdfManager, fileDownloader, numeroDoProcesso, documentos)
            )
        );
        byte[] data = dataHolder.value;
        return data;
    }

    private byte[] gerarPDFProcesso(PdfManager pdfManager, FileDownloader fileDownloader, String numeroDoProcesso,
            List<DocumentoBin> documentos) {
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        try {
            com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document();
            PdfCopy copy = new PdfCopy(pdfDocument, pdf);
            pdfDocument.open();

            String htmlFolhaRosto = "Processo Nr. "+numeroDoProcesso + "Não há documentos";
            if (!documentos.isEmpty()) {
                htmlFolhaRosto="Processo Nr. "+numeroDoProcesso + "possui "+documentos.size()
                        + " documento(s)";
            }
            try(ByteArrayOutputStream pagRosto = new ByteArrayOutputStream()){
                pdfManager.convertHtmlToPdf(htmlFolhaRosto, pagRosto);
                copy = pdfManager.copyPdf(copy, pagRosto.toByteArray());
            }

            for (DocumentoBin documentoBin : documentos) {
                copy = pdfManager.copyPdf(copy, fileDownloader.getData(documentoBin, false));
            }
            pdfDocument.addTitle("numeroDoProcesso");
            pdfDocument.close();
        } catch (DocumentException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Erro", e);
        }
        byte[] data = pdf.toByteArray();
        return data;
    }

    public void autenticar(String username, String password) {
        with(LoginService.class, loginService->{
            if (loginService.autenticar(username, password)) {
            	if (!podeAcessarWSSoap(username)) {
            		throw new WebServiceException(Status.FORBIDDEN.getStatusCode(), "HTTP"+Status.FORBIDDEN.getStatusCode(), "Recurso não disponível");
            	}
			} else {
                throw new WebServiceException(Status.UNAUTHORIZED.getStatusCode(), "HTTP"+Status.UNAUTHORIZED.getStatusCode(), "Não autorizado");
            }
        });
    }
    private static final String RECURSO="acessaWSProcessoSoap";
	private boolean podeAcessarWSSoap(String username) {
		Holder<Boolean> resultado = new Holder<>(Boolean.FALSE);
		with(AuthenticatorService.class, authenticatorService->{
			with(UsuarioPerfilDAO.class, usuarioPerfilDAO->{
				authenticatorService.autenticaManualmenteNoSeamSecurity(username, IdentityManager.instance());
				Set<String> roleSet = new HashSet<>();
				for (UsuarioPerfil usuarioPerfil : usuarioPerfilDAO.listByLogin(username)) {
					roleSet.addAll(RolesMap.instance().getChildrenRoles(usuarioPerfil.getPerfilTemplate().getPapel().getIdentificador()));
				}
				for (String role : roleSet) {
					Identity.instance().addRole(role);
				}
				resultado.value = Identity.instance().hasPermission(RECURSO, "access");
			});
		});
		return resultado.value;
	}
}
