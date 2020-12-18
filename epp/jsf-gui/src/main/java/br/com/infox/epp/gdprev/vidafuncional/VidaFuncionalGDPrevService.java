package br.com.infox.epp.gdprev.vidafuncional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;

@Stateless
public class VidaFuncionalGDPrevService {

    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @Inject
    private VidaFuncionalGDPrevSearch vidaFuncionalGDPrevSearch;

    public void gravarDocumento(DocumentoVidaFuncionalDTO documentoVidaFuncionalDTO, byte[] pdf, Processo processo) {
        String codigoClassificacao = Parametros.CLASSIFICACAO_DOC_PDF_GDPREV.getValue();
        if (StringUtil.isEmpty(codigoClassificacao)) {
            throw new EppConfigurationException(String.format("O parâmetro '%s' não está configurado", Parametros.CLASSIFICACAO_DOC_PDF_GDPREV.getLabel()));
        }
        ClassificacaoDocumento classificacaoDocumento = classificacaoDocumentoManager.findByCodigo(codigoClassificacao);
        if (classificacaoDocumento == null) {
            throw new EppConfigurationException(String.format("A classificação de documento com código '%s' não existe", codigoClassificacao));
        }

        String loginUsuario = Parametros.USUARIO_INCLUSAO_DOC_GDPREV.getValue();
        if (StringUtil.isEmpty(loginUsuario)) {
            throw new EppConfigurationException(String.format("O parâmetro '%s' não está configurado", Parametros.USUARIO_INCLUSAO_DOC_GDPREV.getLabel()));
        }
        UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByLogin(loginUsuario);
        if (usuario == null) {
            throw new EppConfigurationException(String.format("O usuário com login '%s' não existe", loginUsuario));
        }
        if (!usuario.isUsuarioSistema()) {
            throw new EppConfigurationException(String.format("O usuário com login '%s' não é usuário de sistema", loginUsuario));
        }

        DocumentoBin documentoBin = documentoBinManager.createProcessoDocumentoBin(documentoVidaFuncionalDTO.getDescricao(), pdf, "pdf");
        documentoBin.setIdDocumentoExterno(vidaFuncionalGDPrevSearch.gerarIdentificacaoVidaFuncionalGDPrev(documentoVidaFuncionalDTO.getId()));

        Documento documento = new Documento();
        documento.setDataInclusao(documentoVidaFuncionalDTO.getData());
        documento.setDescricao(documentoVidaFuncionalDTO.getDescricao());
        documento.setUsuarioInclusao(usuario);
        documento.setClassificacaoDocumento(classificacaoDocumento);
        documento.setDocumentoBin(documentoBin);

        documentoManager.gravarDocumentoNoProcesso(processo, documento);
        documentoBin.setDataInclusao(documento.getDataInclusao());
        documentoBinManager.update(documentoBin);
    }
}
