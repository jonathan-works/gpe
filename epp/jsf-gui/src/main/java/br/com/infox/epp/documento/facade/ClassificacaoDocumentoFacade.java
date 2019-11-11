package br.com.infox.epp.documento.facade;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.ClassificacaoDocumentoSearch;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;

@Stateless
@Scope(ScopeType.STATELESS)
@Named
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ClassificacaoDocumentoFacade {

    @Inject
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @Inject
    private ClassificacaoDocumentoSearch classificacaoDocumentoSearch;

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

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo) {
        return classificacaoDocumentoManager.getUseableClassificacaoDocumento(isModelo, Authenticator.getPapelAtual());
    }
    
    public List<ClassificacaoDocumento> getUseableClassificacaoDocumentoAnexar(TipoDocumentoEnum tipoDocumento){
        return classificacaoDocumentoManager.getClassificacoesDocumentoAnexarDocumento(tipoDocumento);
    }
    
    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(TipoDocumentoEnum tipoDocumento){
        return classificacaoDocumentoManager.getClassificacoesDocumentoCruds(tipoDocumento);
    }
    
    public List<ClassificacaoDocumento> getUseableClassificacaoDocumentoVariavel(List<String> codigos, boolean isModelo) {
		List<ClassificacaoDocumento> classificacoes = null;
		if (codigos != null && !codigos.isEmpty()) {
			classificacoes = classificacaoDocumentoSearch.findByListCodigos(codigos);
		}
		if (classificacoes == null || classificacoes.isEmpty()) {
			classificacoes = getUseableClassificacaoDocumento(isModelo);
		}
		return classificacoes;
	}
    
}
