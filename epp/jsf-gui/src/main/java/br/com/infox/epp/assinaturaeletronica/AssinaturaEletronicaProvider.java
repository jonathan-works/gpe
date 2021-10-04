package br.com.infox.epp.assinaturaeletronica;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.type.LocalizacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.documento.type.OrientacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class AssinaturaEletronicaProvider {

    private static final LogProvider LOG = Logging.getLogProvider(AssinaturaEletronicaProvider.class);

    private static LocalizacaoAssinaturaEletronicaDocumentoEnum LOCALIZACAO_PADRAO = LocalizacaoAssinaturaEletronicaDocumentoEnum.ULTIMA_PAGINA;
    private static OrientacaoAssinaturaEletronicaDocumentoEnum ORIENTACAO_PADRAO = OrientacaoAssinaturaEletronicaDocumentoEnum.RODAPE_HORIZONTAL;

    private DocumentoBin documentoBin;

    public AssinaturaEletronicaProvider(DocumentoBin documentoBin) {
        this.documentoBin = documentoBin;
    }

    public ClassificacaoDocumento getClassificacaoDocumento() {
        ClassificacaoDocumento classificDoc = null;
        if(!CollectionUtil.isEmpty(documentoBin.getDocumentoList())) {
            Documento documento = documentoBin.getDocumentoList().get(0);
            classificDoc = documento.getClassificacaoDocumento();
        } else if (!CollectionUtil.isEmpty(documentoBin.getDocumentoTemporarioList())) {
            DocumentoTemporario docTemp = documentoBin.getDocumentoTemporarioList().get(0);
            classificDoc = docTemp.getClassificacaoDocumento();
        }

        return classificDoc;
    }

    public LocalizacaoAssinaturaEletronicaDocumentoEnum getLocalizacaoAssinatura() {
        ClassificacaoDocumento classificDoc = getClassificacaoDocumento();
        if(classificDoc != null && documentoBin.isBinario()) {
            return Optional.ofNullable(classificDoc.getLocalizacaoAssinaturaEletronicaDocumentoEnum()).orElse(LOCALIZACAO_PADRAO);
        } else {
            return LOCALIZACAO_PADRAO;
        }
    }

    public OrientacaoAssinaturaEletronicaDocumentoEnum getOrientacaoAssinatura() {
        ClassificacaoDocumento classificDoc = getClassificacaoDocumento();
        if(classificDoc != null && documentoBin.isBinario()) {
            return Optional.ofNullable(classificDoc.getOrientacaoAssinaturaEletronicaDocumentoEnum()).orElse(ORIENTACAO_PADRAO);
        } else {
            return ORIENTACAO_PADRAO;
        }
    }

    public int getPaginaUnica() {
        ClassificacaoDocumento classificDoc = getClassificacaoDocumento();
        if(classificDoc != null) {
            return Optional.ofNullable(classificDoc.getPaginaExibicaoAssinaturaEletronica()).orElse(0);
        } else {
            return 0;
        }
    }

    public List<byte[]> getImagensAssinatura(){
        AssinaturaEletronicaBinSearch assinaturaEletronicaBinSearch = Beans.getReference(AssinaturaEletronicaBinSearch.class);
        AssinaturaEletronicaSearch assinaturaEletronicaSearch = Beans.getReference(AssinaturaEletronicaSearch.class);

        List<byte[]> imagensAssinatura = new ArrayList<>();
        try {
            for (AssinaturaDocumento assinatura : documentoBin.getAssinaturas()) {
                AssinaturaEletronica assinaturaEletronica = assinaturaEletronicaSearch.getAssinaturaEletronicaByIdPessoaFisica(assinatura.getPessoaFisica().getIdPessoa());
                byte[] imgAssinatura = null;
                if(assinaturaEletronica != null) {
                    imgAssinatura = assinaturaEletronicaBinSearch.getImagemByIdAssinaturaEletronicaBin(assinaturaEletronica.getId());
                }

                try {
                    byte[] imagem = AssinaturaEletronicaImagemBuilder.gerarImagemAssinaturaEletronica(imgAssinatura, assinatura.getNomeUsuario(), assinatura.getPapel().getNome());
                    imagensAssinatura.add(imagem);
                } catch (IOException e) {
                    String msgErro = String.format("Não foi possível gerar imagem da assinatura para a pessoa: NOME=%s, PAPEL=%s", assinatura.getNomeUsuario(), assinatura.getPapel().getNome());
                    LOG.warn(".getImagensAssinatura " + msgErro, e);
                }
            }
        } finally {
            closeCdiResource(assinaturaEletronicaBinSearch);
            closeCdiResource(assinaturaEletronicaSearch);
        }

        return imagensAssinatura;
    }

    private void closeCdiResource(Object object) {
        if (object != null) {
            Beans.destroy(object);
        }
    }


}
