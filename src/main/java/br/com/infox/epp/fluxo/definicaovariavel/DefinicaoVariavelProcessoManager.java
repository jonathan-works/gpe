package br.com.infox.epp.fluxo.definicaovariavel;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoVariavelProcessoManager {

    public static final String JBPM_VARIABLE_TYPE = "processo";
    
    @Inject
    private DefinicaoVariavelProcessoSearch definicaoVariavelProcessoSearch;
    @Inject
    @GenericDao
    private Dao<DefinicaoVariavelProcesso, Long> definicaoVariavelProcessoDAO;
    @Inject
    @GenericDao
    private Dao<DefinicaoVariavelProcessoRecurso, Long> definicaoVariavelProcessoRecursoDAO;
    @Inject
    private FluxoManager fluxoManager;

    public List<DefinicaoVariavelProcesso> getDefinicaoVariavelProcessoVisivelPainel(Integer idFluxo) {
        return definicaoVariavelProcessoSearch.getDefinicoesVariaveis(fluxoManager.find(idFluxo), 
        		DefinicaoVariavelProcessoRecursos.PAINEL_INTERNO.getIdentificador(), false);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persist(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	definicaoVariavelProcessoDAO.persist(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoVariavelProcesso update(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	return definicaoVariavelProcessoDAO.update(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(DefinicaoVariavelProcesso definicaoVariavelProcesso) throws DAOException {
    	for (DefinicaoVariavelProcessoRecurso recurso : definicaoVariavelProcessoSearch.getRecursos(definicaoVariavelProcesso)) {
    		definicaoVariavelProcessoRecursoDAO.remove(recurso);
    	}
    	definicaoVariavelProcessoDAO.remove(definicaoVariavelProcesso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<DefinicaoVariavelProcesso> createDefaultDefinicaoVariavelProcessoList(Fluxo fluxo) {
        List<DefinicaoVariavelProcesso> dvpList = new ArrayList<>(7);
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "numeroProcesso", "Número do Processo", null, 0));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "usuarioCadastro", "Usuário Solicitante", "#{variavelProcessoService.getUsuarioCadastro}", 1));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "dataInicioProcesso", "Data Início", null, 2));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "naturezaProcesso", "Natureza", null, 3));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "categoriaProcesso", "Categoria", null, 4));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "prioridadeProcesso", "Prioridade do Processo", "#{variavelProcessoService.getPrioridadeProcesso}", 5));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "itemProcesso", "Item", null, 6));
        return dvpList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private DefinicaoVariavelProcesso createDefaultDefinicaoVariavelProcesso(Fluxo fluxo, String nome, String label, String valorPadrao, Integer ordem) {
        DefinicaoVariavelProcesso dvp = new DefinicaoVariavelProcesso();
        dvp.setNome(nome);
        dvp.setLabel(label);
        dvp.setFluxo(fluxo);
        dvp.setValorPadrao(valorPadrao);
        dvp.setVersion(0L);
        persist(dvp);
        DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso = new DefinicaoVariavelProcessoRecurso();
        definicaoVariavelProcessoRecurso.setDefinicaoVariavelProcesso(dvp);
        definicaoVariavelProcessoRecurso.setRecurso(DefinicaoVariavelProcessoRecursos.PAINEL_INTERNO.getIdentificador());
        definicaoVariavelProcessoRecurso.setOrdem(ordem);
        definicaoVariavelProcessoRecursoDAO.persist(definicaoVariavelProcessoRecurso);
        definicaoVariavelProcessoRecurso = new DefinicaoVariavelProcessoRecurso();
        definicaoVariavelProcessoRecurso.setDefinicaoVariavelProcesso(dvp);
        definicaoVariavelProcessoRecurso.setRecurso(DefinicaoVariavelProcessoRecursos.CONSULTA_PROCESSOS.getIdentificador());
        definicaoVariavelProcessoRecurso.setOrdem(ordem);
        definicaoVariavelProcessoRecursoDAO.persist(definicaoVariavelProcessoRecurso);
        return dvp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removerRecurso(DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso) {
    	definicaoVariavelProcessoRecursoDAO.remove(definicaoVariavelProcessoRecurso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void adicionarRecurso(DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso) {
    	definicaoVariavelProcessoRecursoDAO.persist(definicaoVariavelProcessoRecurso);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefinicaoVariavelProcessoRecurso atualizarRecurso(DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso) {
    	return definicaoVariavelProcessoRecursoDAO.update(definicaoVariavelProcessoRecurso);
    }
}
