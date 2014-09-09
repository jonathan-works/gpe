package br.com.infox.epp.documento.facade;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.fluxo.manager.VariavelClassificacaoDocumentoManager;

@Name(ClassificacaoDocumentoFacade.NAME)
@Scope(ScopeType.CONVERSATION)
public class ClassificacaoDocumentoFacade {

    @In
    TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    @In
    private VariavelClassificacaoDocumentoManager variavelClassificacaoDocumentoManager;

    public static final String NAME = "classificacaoDocumentoFacade";

    public TipoDocumentoEnum[] getTipoDocumentoEnumValues() {
        return TipoDocumentoEnum.values();
    }

    public TipoNumeracaoEnum[] getTipoNumeracaoEnumValues() {
        return TipoNumeracaoEnum.values();
    }

    public VisibilidadeEnum[] getVisibilidadeEnumValues() {
        return VisibilidadeEnum.values();
    }
    
    public TipoAssinaturaEnum[] getTipoAssinaturaEnumValues() {
        return TipoAssinaturaEnum.values();
    }

    public List<TipoProcessoDocumento> getUseableTipoProcessoDocumento(boolean isModelo, String nomeVariavel, Integer idFluxo) {
        List<TipoProcessoDocumento> classificacoes = variavelClassificacaoDocumentoManager.listClassificacoesPublicadasDaVariavel(nomeVariavel, idFluxo);
        if (!classificacoes.isEmpty()) {
            return classificacoes;
        }
        return getUseableTipoProcessoDocumento(isModelo);
    }
    
    public List<TipoProcessoDocumento> getUseableTipoProcessoDocumento(boolean isModelo) {
        return tipoProcessoDocumentoManager.getUseableTipoProcessoDocumento(isModelo, Authenticator.getPapelAtual());
    }
}
