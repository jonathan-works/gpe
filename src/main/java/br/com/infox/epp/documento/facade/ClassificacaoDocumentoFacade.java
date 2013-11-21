package br.com.infox.epp.documento.facade;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.documento.type.VisibilidadeEnum;

@Name(ClassificacaoDocumentoFacade.NAME)
@Scope(ScopeType.CONVERSATION)
public class ClassificacaoDocumentoFacade {
    
    @In TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    
    public static final String NAME = "classificacaoDocumentoFacade";
    
    public TipoDocumentoEnum[] getTipoDocumentoEnumValues() {
        return TipoDocumentoEnum.values();
    }
    
    public TipoNumeracaoEnum[] getTipoNumeracaoEnumValues() {
        return TipoNumeracaoEnum.values();
    }
    
    public VisibilidadeEnum[] getVisibilidadeEnumValues(){
        return VisibilidadeEnum.values();
    }
    
    public List<TipoProcessoDocumento> getTipoProcessoDocumentoInterno(boolean isModelo){
        return tipoProcessoDocumentoManager.getTipoProcessoDocumentoInterno(isModelo);
    }

}
