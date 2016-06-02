package br.com.infox.ibpm.event;

import static br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais.PROCESSO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.comunicacao.prazo.ContabilizadorPrazo;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.ibpm.event.External.ExpressionType;
import br.com.infox.ibpm.sinal.SignalService;

@Stateless
@Named(BpmExpressionService.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EppBpmExpressionService extends BpmExpressionService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ContabilizadorPrazo contabilizadorPrazo = BeanManager.INSTANCE.getReference(ContabilizadorPrazo.class);
    private PastaManager pastaManager = BeanManager.INSTANCE.getReference(PastaManager.class);
    @Inject
    private SignalService signalService;
    @Inject
    private DocumentoManager documentoManager;

    @External(tooltip = "process.events.expression.atribuirCiencia.tooltip")
    public void atribuirCiencia() {
        contabilizadorPrazo.atribuirCiencia();
    }

    @External(tooltip = "process.events.expression.atribuirCumprimento.tooltip")
    public void atribuirCumprimento() {
        contabilizadorPrazo.atribuirCumprimento();
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.disponibilizarPastaParaParticipantesProcesso.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso)
            throws DAOException {
        pastaManager.disponibilizarPastaParaParticipantesProcesso(descricaoPasta, idProcesso);
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.tornarPastaPublica.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void tornarPastaPublica(String nomePasta, Long processo) throws DAOException {
        pastaManager.tornarPastaPublica(nomePasta, processo);
    }
    
    @External(value = {
            @Parameter(defaultValue = "'Código do Sinal'", label = "process.events.expression.param.codigoSinal.label", tooltip = "process.events.expression.param.codigoSinal.tooltip", selectable = true)
            })
    public void dispatchSignal(String codigoSinal) throws DAOException {
        signalService.dispatch(codigoSinal);
    }
    
	@External(expressionType = ExpressionType.RAIA_DINAMICA, 
		tooltip = "process.events.expression.stringListBuilder.tooltip",
		example = "#{bpmExpressionService.stringListBuilder().add(variavelString).add('string').add(variavelListaString).build()}"
	)
    public StringListBuilder stringListBuilder() {
		return new StringListBuilder();
    }
	
	@External(expressionType = ExpressionType.GERAL, value = {
        @Parameter(defaultValue = "'Nome variável editor/upload'", label = "process.events.expression.param.suficientementeAssinado.label", 
                tooltip = "process.events.expression.param.suficientementeAssinado.tooltip", selectable = true)
    })
    public boolean isDocumentoSuficientementeAssinado(Integer idDocumento) throws DAOException {
	    boolean suficientementeAssinado = false;
	    if (idDocumento != null) {
	        Documento documento = documentoManager.find(idDocumento);
	        if (documento != null) {
	            suficientementeAssinado = documento.getDocumentoBin().getSuficientementeAssinado();
	        }
	    }
	    return suficientementeAssinado;
    }
	
	@External(expressionType = ExpressionType.RAIA_DINAMICA, 
        tooltip = "process.events.expression.toList.tooltip",
        example = "#{bpmExpressionService.toList(variavel).put(variavelLista).put(variavel2))}"
    )
    public Collection<Object> toList(Object object) {
	    return new ObjectCollection(object.getClass()).put(object);
    }

    @Override
    public List<ExternalMethod> getExternalMethods() {
    	return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GERAL);
    }

	@Override
	public List<ExternalMethod> getExternalRaiaDinamicaMethods() {
		return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.RAIA_DINAMICA);
	}
s
	@Override
	public List<ExternalMethod> getExternalGatewayMethods() {
	    return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GATEWAY);
	}
}
