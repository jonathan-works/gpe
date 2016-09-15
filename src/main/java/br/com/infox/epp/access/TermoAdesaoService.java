package br.com.infox.epp.access;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import com.lowagie.text.DocumentException;

import br.com.infox.core.file.download.DocumentoServlet;
import br.com.infox.core.file.download.DocumentoServletOperation;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.seam.exception.BusinessException;

@Stateless
public class TermoAdesaoService {

    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private DocumentoBinarioManager documentoBinarioManager;
    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private PdfManager pdfManager;
    @Inject
    private UsuarioLoginSearch usuarioLoginSearch;
    
    public String buildUrlDownload(String contextPath, String jwt, String uidTermoAdesao) {
        UriBuilder uriBuilder = UriBuilder.fromPath(contextPath);
        uriBuilder = uriBuilder.path(DocumentoServlet.BASE_SERVLET_PATH);
        uriBuilder = uriBuilder.path(uidTermoAdesao);
        uriBuilder = uriBuilder.path(DocumentoServletOperation.DOWNLOAD.getPath());
        return uriBuilder.queryParam("jwt", jwt).build().toString();
    }
    
    public boolean isTermoAdesaoAssinado(String cpf){
        return usuarioLoginSearch.getAssinouTermoAdesao(cpf);
    }
    
    public DocumentoBin createTermoAdesaoFor(PessoaFisica pessoaFisica){
        if (pessoaFisica == null) {
            throw new BusinessException("termoAdesao.authorization.fail");
        }
        if (isTermoAdesaoAssinado(pessoaFisica.getCpf())){
            return pessoaFisica.getTermoAdesao();
        }
        String tituloTermoAdesao = Parametros.TERMO_ADESAO.getValue();
        ModeloDocumento modeloDocumento = modeloDocumentoManager.getModeloDocumentoByTitulo(tituloTermoAdesao);
        Map<String, String> variables = getTermoAdesaoVariables(pessoaFisica);
        byte[] termoAdesao;
        try {
             termoAdesao = getTermoAdesaoByteArray(modeloDocumento, variables);
        } catch (DocumentException e){
            throw new BusinessException("termoAdesao.conversion.fail");
        }
        return createTermoAdesao(pessoaFisica, tituloTermoAdesao, termoAdesao);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private DocumentoBin createTermoAdesao(PessoaFisica pessoaFisica, String tituloTermoAdesao, byte[] termoAdesao) {
        removeIfExists(pessoaFisica.getTermoAdesao());
        DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin(tituloTermoAdesao, termoAdesao, "pdf");
        bin = documentoBinManager.createProcessoDocumentoBin(bin);
        pessoaFisica.setTermoAdesao(bin);
        pessoaFisica = pessoaFisicaManager.update(pessoaFisica);
        return bin;
    }

    private void removeIfExists(DocumentoBin o) {
        if ( o != null) {
            documentoBinManager.remove(o);
            documentoBinarioManager.remove(o.getId());
        }
    }
    
    private byte[] getTermoAdesaoByteArray(ModeloDocumento modeloDocumento, Map<String, String> variables) throws DocumentException {
        String termoAdesao = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, getExpressionResolver(variables));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfManager.convertHtmlToPdf(termoAdesao, outputStream);
        return outputStream.toByteArray();
    }
    
    private Map<String, String> getTermoAdesaoVariables(PessoaFisica pessoaFisica) {
        HashMap<String, String> variaveis = new HashMap<>();
        variaveis.put("#{usuarioLogado}", pessoaFisica.getNome());
        variaveis.put("#{CPF_pessoa_logada}", pessoaFisica.getCodigoFormatado());
        variaveis.put("#{Data_nascimento_pessoa_logada}", pessoaFisica.getDataFormatada());
        MeioContato email = pessoaFisica.getMeioContato(TipoMeioContatoEnum.EM);
        if (pessoaFisica.getUsuarioLogin() != null){
            variaveis.put("#{emailUsuarioLogado}", pessoaFisica.getUsuarioLogin().getEmail());
        } else {
            if (email != null)
                variaveis.put("#{emailUsuarioLogado}", email.getMeioContato());
            else
                variaveis.put("#{emailUsuarioLogado}", "-");
        }
        MeioContato fixo = pessoaFisica.getMeioContato(TipoMeioContatoEnum.TF);
        if (email != null)
            variaveis.put("#{Telefone_pessoa_logada}", fixo.getMeioContato());
        else
            variaveis.put("#{Telefone_pessoa_logada}", "-");
        return variaveis;
    }

    private ExpressionResolverChain getExpressionResolver(Map<String, String> variables) {
        return ExpressionResolverChainBuilder.with(new ArbitraryExpressionResolver(variables)).and(new SeamExpressionResolver()).build();
    }
}
