package br.com.infox.epp.documento.facade;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.inject.Inject;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;
import br.com.infox.epp.fluxo.manager.VariavelClassificacaoDocumentoManager;

@Stateless
@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(ClassificacaoDocumentoFacade.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ClassificacaoDocumentoFacade {

    @In
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @Inject
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

    public List<ClassificacaoDocumento> getUseableClassificacaoDocumento(boolean isModelo, String nomeVariavel, Integer idFluxo) {
    	if (nomeVariavel != null) {
	        List<ClassificacaoDocumento> classificacoes = variavelClassificacaoDocumentoManager.listClassificacoesPublicadasDaVariavel(nomeVariavel, idFluxo);
	        if (!classificacoes.isEmpty()) {
	            return classificacoes;
	        }
    	}
        return getUseableClassificacaoDocumento(isModelo);
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
    
}
