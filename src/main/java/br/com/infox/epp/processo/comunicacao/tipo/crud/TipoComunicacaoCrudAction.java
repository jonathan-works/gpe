package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@Name(TipoComunicacaoCrudAction.NAME)
public class TipoComunicacaoCrudAction extends AbstractCrudAction<TipoComunicacao, TipoComunicacaoManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoCrudAction";
    
    private List<TipoModeloDocumento> tiposModeloDocumento;
    private List<ClassificacaoDocumento> classificacoesDocumento;
    
    @In
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    
    @In
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;
    
    public List<TipoModeloDocumento> getTiposModeloDocumento() {
    	if (tiposModeloDocumento == null) {
    		tiposModeloDocumento = tipoModeloDocumentoManager.getTiposModeloDocumentoAtivos();
    	}
		return tiposModeloDocumento;
	}
    
    public List<ClassificacaoDocumento> getClassificacoesDocumento() {
        if (classificacoesDocumento == null) {
            classificacoesDocumento = classificacaoDocumentoFacade
                    .getUseableClassificacaoDocumento(TipoDocumentoEnum.P);
        }
        return classificacoesDocumento;
    }
}
