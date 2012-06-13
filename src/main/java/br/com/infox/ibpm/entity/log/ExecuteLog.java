/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.entity.log;

import javax.persistence.EntityManager;

import org.hibernate.AssertionFailure;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.util.StopWatch;

import br.com.infox.type.TipoOperacaoLogEnum;
import br.com.itx.util.ArrayUtil;

/**
 * 
 * @author Rodrigo Menezes
 *
 */
public class ExecuteLog {
	
	private static final LogProvider log = Logging.getLogProvider(ExecuteLog.class);	
	private Object[] oldState;
	private Object[] state;
	private EntityPersister persister;
	private Object entidade;
	private TipoOperacaoLogEnum tipoOperacao;
	private EntityManager em;
	private StopWatch sw;
	
	public ExecuteLog() {
		sw = new StopWatch(true);
		em = (EntityManager) Component.getInstance("entityManagerLog");
	}

	public Object[] getOldState() {
		return ArrayUtil.copyOf(oldState);
	}

	public void setOldState(Object[] oldState) {
		this.oldState = ArrayUtil.copyOf(oldState);
	}

	public void setState(Object[] state) {
		this.state = ArrayUtil.copyOf(state);
	}

	public EntityPersister getPersister() {
		return persister;
	}

	public void setPersister(EntityPersister persister) {
		this.persister = persister;
	}

	public Object getEntidade() {
		return entidade;
	}

	public void setEntidade(Object entidade) {
		this.entidade = entidade;
	}

	public TipoOperacaoLogEnum getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(TipoOperacaoLogEnum tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	private void init() {
		if (tipoOperacao.equals(TipoOperacaoLogEnum.I)) {
			oldState = new Object[state.length];
		} else if (tipoOperacao.equals(TipoOperacaoLogEnum.D)) {
			state = new Object[oldState.length];			
		}
	}	
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void execute() {
		if (! Contexts.isSessionContextActive()) {
			return;
		}
		
		init();
		
		String[] nomes = persister.getClassMetadata().getPropertyNames();
		EntityLog logEnt = LogUtil.createEntityLog(entidade);
		logEnt.setTipoOperacao(tipoOperacao);
		
		em.persist(logEnt);
		
		for (int i = 0; i < nomes.length; i++) {
			try {
				if(!LogUtil.isCollection(entidade, nomes[i]) && 
						!LogUtil.isBinario(entidade, nomes[i]) && 
						!LogUtil.compareObj(oldState[i], state[i])) {
					EntityLogDetail detail = new EntityLogDetail();
					detail.setEntityLog(logEnt);
					detail.setNomeAtributo(nomes[i]);
					detail.setValorAtual(LogUtil.toStringForLog(state[i]));
					detail.setValorAnterior(LogUtil.toStringForLog(oldState[i]));
					em.persist(detail);
					logEnt.getLogDetalheList().add(detail);
				}
			} catch (Exception e) {
				log.error("Erro ao logar", e);
				e.printStackTrace();
			} 
		}
		try {
			em.flush();
		} catch (AssertionFailure e) { /* Bug do hibernate: HHH-2763 */ }
		
		StringBuilder sb = new StringBuilder();
		sb.append(".execute(): ").append(tipoOperacao.getLabel());
		sb.append(" (").append(entidade.getClass().getName()).append("): ");
		sb.append(sw.getTime());
		log.info(sb.toString());
	}


}