package br.com.infox.epp.processo.consulta.list;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoSearch;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class ConsultaProcessoDynamicColumnsController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final String DYNAMIC_COLUMN_EXPRESSION = "#'{'consultaProcessoDynamicColumnsController.getValor(row, ''{0}'')'}";
	
	@Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    private VariavelProcessoService variavelProcessoService;
    @Inject
    private PapelManager papelManager;

    private List<String> controleMensagensValidacao = new ArrayList<>();
	private List<DynamicColumnModel> dynamicColumns;
	private Fluxo fluxo;
	
	public List<DynamicColumnModel> getDynamicColumns() {
    	if (dynamicColumns == null) {
    		dynamicColumns = new ArrayList<>();
    		boolean usuarioExterno = Authenticator.getPapelAtual() != null ? papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()) : true;
    		List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxo, 
    				DefinicaoVariavelProcessoRecursos.CONSULTA_PROCESSOS.getIdentificador(), usuarioExterno);
    		
    		for (DefinicaoVariavelProcesso definicaoVariavel : definicoes) {
    			DynamicColumnModel model = new DynamicColumnModel(definicaoVariavel.getLabel(), MessageFormat.format(DYNAMIC_COLUMN_EXPRESSION, definicaoVariavel.getNome()));
    			dynamicColumns.add(model);
    		}
    	}
		return dynamicColumns;
	}
    
    public String getValor(Processo processo, String nomeVariavel) {
    	try {
	    	VariavelProcesso variavel = variavelProcessoService.getVariavelProcesso(processo.getIdProcesso(), nomeVariavel);
	    	if (variavel != null) {
	    		return variavel.getValor();
	    	}
    	} catch (BusinessException e) {
    		if (!controleMensagensValidacao.contains(e.getMessage())) {
    			FacesMessages.instance().add(e.getMessage());
    			controleMensagensValidacao.add(e.getMessage());
    		}
    	}
    	return null;
    }
    
    public List<Fluxo> getFluxos(String search) {
    	CriteriaBuilder cb = EntityManagerProducer.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Fluxo> query = cb.createQuery(Fluxo.class);
    	Root<Processo> processo = query.from(Processo.class);
    	Join<Processo, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
    	Join<NaturezaCategoriaFluxo, Fluxo> fluxo = ncf.join(NaturezaCategoriaFluxo_.fluxo, JoinType.INNER);
    	query.select(fluxo);
    	query.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
    	query.distinct(true);
    	if (!Strings.isNullOrEmpty(search)) {
    		query.where(cb.like(cb.lower(fluxo.get(Fluxo_.fluxo)), "%" + search.toLowerCase() + "%"));
    	}
    	return EntityManagerProducer.getEntityManager().createQuery(query).getResultList();
	}
    
    public void clearMensagensValidacao() {
    	controleMensagensValidacao = new ArrayList<>();
    }

    public Fluxo getFluxo() {
		return fluxo;
	}
    
    public void setFluxo(Fluxo fluxo) {
    	if (fluxo == null || !Objects.equals(fluxo, this.fluxo)) {
    		this.fluxo = fluxo;
    		clearMensagensValidacao();
        	dynamicColumns = null;
    	}
	}
}
