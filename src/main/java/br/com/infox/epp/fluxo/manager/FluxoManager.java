package br.com.infox.epp.fluxo.manager;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.entity.RaiaPerfil;
import br.com.infox.ibpm.util.BpmUtil;

@Name(FluxoManager.NAME)
@AutoCreate
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@ContextDependency
public class FluxoManager extends Manager<FluxoDAO, Fluxo> {

    private static final long serialVersionUID = -6521661616139554331L;

    public static final String NAME = "fluxoManager";

    @In
    private RaiaPerfilManager raiaPerfilManager;
    @Inject
    private InfoxMessages infoxMessages;
    
    private boolean isValidDataFim(Fluxo fluxo, Date now){
        final Date date = fluxo.getDataFimPublicacao();
        return date==null || date.after(now);
    }
    
    private boolean isValidDataInicio(Fluxo fluxo, Date now){
        final Date date = fluxo.getDataInicioPublicacao();
        return date!=null && date.before(now);
    }
    
    private boolean isValidUsuarioPerfil(Fluxo fluxo, UsuarioPerfil usuarioPerfil) {
        for (FluxoPapel fluxoPapel : fluxo.getFluxoPapelList()) {
            if (Objects.equals(fluxoPapel.getPapel(), usuarioPerfil.getPerfilTemplate().getPapel())){
                return true;
            }
        }
        return false;
    }

    private void beforePersistOrUpdate(Fluxo o) {
        updateDataInicioPublicacao(o);
        updateDataFimPublicacao(o);
    }
    
    private void updateDataInicioPublicacao(Fluxo o) {
        o.setDataInicioPublicacao(DateUtil.getBeginningOfDay(o.getDataInicioPublicacao()));
    }

    private void updateDataFimPublicacao(Fluxo o) {
        if (o.getDataFimPublicacao() != null){
            o.setDataFimPublicacao(DateUtil.getEndOfDay(o.getDataFimPublicacao()));
        }
    }
    
    public List<Fluxo> getFluxosAtivosList() {
        return getDao().getFluxosAtivosList();
    }

    public boolean contemProcessoAtrasado(final Fluxo fluxo) {
        return getDao().quantidadeProcessosAtrasados(fluxo) > 0;
    }

    public Fluxo getFluxoByDescricao(final String descricao) {
        return getDao().getFluxoByDescricao(descricao);
    }

    public Fluxo getFluxoByCodigo(final String codigo) {
        return getDao().getFluxoByCodigo(codigo);
    }
    
    public boolean existemProcessosAssociadosAFluxo(final Fluxo fluxo) {
        return getDao().getQuantidadeDeProcessoAssociadosAFluxo(fluxo) > 0;
    }

    public boolean existeFluxoComDescricao(final String descricao) {
        return getDao().existeFluxoComDescricao(descricao);
    }

    public boolean existeFluxoComCodigo(final String codigo) {
        return getDao().existeFluxoComCodigo(codigo);
    }

    public Collection<Integer> getIdsLocalizacoesRaias(final Fluxo fluxo) {
        Set<Integer> idsLocalizacao = new HashSet<Integer>();
        List<RaiaPerfil> listByFluxo = raiaPerfilManager.listByFluxo(fluxo);
        for (RaiaPerfil raiaPerfil : listByFluxo) {
            idsLocalizacao.add(raiaPerfil.getPerfilTemplate().getLocalizacao().getIdLocalizacao());
        }
        return idsLocalizacao;
    }

    @Override
    public Fluxo persist(Fluxo o) throws DAOException {
        beforePersistOrUpdate(o);
        return super.persist(o);
    }

    @Override
    public Fluxo update(Fluxo o) throws DAOException {
        beforePersistOrUpdate(o);
        return super.update(o);
    }

    public boolean isFluxoInicializavel(Fluxo fluxo, UsuarioPerfil usuarioPerfil){
        Date now = new Date();
        return fluxo.getPublicado() && isValidDataInicio(fluxo, now) && isValidDataFim(fluxo, now)
                && isValidUsuarioPerfil(fluxo, usuarioPerfil);
    }

    public ProcessDefinition createInitialProcessDefinition() {
    	ProcessDefinition processDefinition = ProcessDefinition.createNewProcessDefinition();
    	processDefinition.setKey(BpmUtil.generateKey());
        Swimlane laneSolicitante = new Swimlane("Solicitante");
        laneSolicitante.setKey(BpmUtil.generateKey());
        laneSolicitante.setActorIdExpression("#{actor.id}");

        Task startTask = new Task("Tarefa inicial");
        startTask.setKey(BpmUtil.generateKey());
        startTask.setSwimlane(laneSolicitante);
        processDefinition.getTaskMgmtDefinition().setStartTask(startTask);

        StartState startState = new StartState(infoxMessages.get("process.node.first"));
        startState.setKey(BpmUtil.generateKey());
        processDefinition.addNode(startState);
        EndState endState = new EndState(infoxMessages.get("process.node.last"));
        endState.setKey(BpmUtil.generateKey());
        processDefinition.addNode(endState);
        Transition t = new Transition();
        t.setKey(BpmUtil.generateKey());
        t.setName(endState.getName());
        t.setTo(endState);
        startState.addLeavingTransition(t);
        endState.addArrivingTransition(t);
        processDefinition.getTaskMgmtDefinition().addSwimlane(laneSolicitante);
        
        return processDefinition;
    }
}
