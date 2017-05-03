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
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos.RecursoVariavel;
import br.com.infox.epp.fluxo.entity.Fluxo;

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
    private DefinicaoVariavelProcessoRecursos definicaoVariavelProcessoRecursos;

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
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "numeroProcesso", "Número do Processo", "#{numeroProcesso}", 0));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "usuarioCadastro", "Usuário Solicitante", "#{variavelProcessoService.getUsuarioCadastro}", 1));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "dataInicioProcesso", "Data Início", "#{dataInicioProcesso}", 2));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "naturezaProcesso", "Natureza", "#{naturezaProcesso}", 3));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "categoriaProcesso", "Categoria", "#{categoriaProcesso}", 4));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "prioridadeProcesso", "Prioridade do Processo", "#{variavelProcessoService.getPrioridadeProcesso}", 5));
        dvpList.add(createDefaultDefinicaoVariavelProcesso(fluxo, "itemProcesso", "Item", "#{itemProcesso}", 6));
        return dvpList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private DefinicaoVariavelProcesso createDefaultDefinicaoVariavelProcesso(Fluxo fluxo, String nome, String label, String valorPadrao, Integer ordem) {
        DefinicaoVariavelProcesso dvp = new DefinicaoVariavelProcesso();
        dvp.setLabel(label);
        dvp.setFluxo(fluxo);
        dvp.setValorPadrao(valorPadrao);
        dvp.setVersion(0L);
        persist(dvp);
        for (RecursoVariavel recurso : definicaoVariavelProcessoRecursos.getRecursosDisponiveis()) {
        	DefinicaoVariavelProcessoRecurso definicaoVariavelProcessoRecurso = new DefinicaoVariavelProcessoRecurso();
            definicaoVariavelProcessoRecurso.setDefinicaoVariavelProcesso(dvp);
            definicaoVariavelProcessoRecurso.setRecurso(recurso.getIdentificador());
            definicaoVariavelProcessoRecurso.setOrdem(ordem);
            boolean visivelExterno = !nome.equals("prioridadeProcesso") && !nome.equals("usuarioCadastro");
            definicaoVariavelProcessoRecurso.setVisivelUsuarioExterno(visivelExterno);
            definicaoVariavelProcessoRecursoDAO.persist(definicaoVariavelProcessoRecurso);
        }
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
    
    public DefinicaoVariavelProcessoRecurso getRecursoById(Long id) {
    	return definicaoVariavelProcessoRecursoDAO.findById(id);
    }
}
