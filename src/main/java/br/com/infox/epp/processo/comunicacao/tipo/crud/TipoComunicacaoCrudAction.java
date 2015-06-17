package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(TipoComunicacaoCrudAction.NAME)
public class TipoComunicacaoCrudAction extends AbstractCrudAction<TipoComunicacao, TipoComunicacaoManager> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(TipoComunicacaoCrudAction.class);
    
    private List<TipoModeloDocumento> tiposModeloDocumento;
    private List<ClassificacaoDocumento> classificacoesDocumento;
    @In
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @In
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;
    @In
    private GenericManager genericManager;
    @In
    private ActionMessagesService actionMessagesService;
    
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
    
    public void addClassificacaoDocumentoResposta(ClassificacaoDocumento classificacaoDocumento){
    	TipoComunicacaoClassificacaoDocumento tipoComunicacaoClassificacaoDocumento = new TipoComunicacaoClassificacaoDocumento();
    	tipoComunicacaoClassificacaoDocumento.setClassificacaoDocumento(classificacaoDocumento);
    	tipoComunicacaoClassificacaoDocumento.setTipoComunicacao(getInstance());
    	try {
			getInstance().getTipoComunicacaoClassificacaoDocumentos().add((TipoComunicacaoClassificacaoDocumento) genericManager.persist(tipoComunicacaoClassificacaoDocumento));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
    }
    
    public void removeClassificacaoDocumentoResposta(TipoComunicacaoClassificacaoDocumento tipoComunicacaoClassificacaoDocumento) {
    	try {
			genericManager.remove(tipoComunicacaoClassificacaoDocumento);
			getInstance().getTipoComunicacaoClassificacaoDocumentos().remove(tipoComunicacaoClassificacaoDocumento);
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
}
