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
package br.com.infox.ibpm.component;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.dao.FluxoDAO;
import br.com.infox.ibpm.entity.Estatistica;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Processo;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name("estatistica")
@Install(precedence=FRAMEWORK)
@Scope(ScopeType.SESSION)
public class RegistraEstatistica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In private FluxoDAO fluxoDAO;
	
	
	public void registraAssignTask() {
		//Fazer as atribui��es das tarefas
	}
	
	public void registraAssignTask(Date dataCadastro, Processo processo, 
			ExecutionContext context) {
		String taskName = context.getTaskInstance().getTask().getName();
		String nomeFluxo = context.getProcessDefinition().getName();		
		EntityManager em = EntityUtil.getEntityManager();
		Estatistica e = new Estatistica();
		e.setDataInicio(dataCadastro);
		e.setProcesso(processo);
		e.setTaskName(taskName);
		e.setNomeFluxo(nomeFluxo);
		e.setFluxo(getFluxo(nomeFluxo));
		em.persist(e);
		EntityUtil.flush(em);
		processo.getEstatisticaList().add(e);
	}	
	
	private Fluxo getFluxo(String nomeFluxo) {
		Fluxo fluxo = fluxoDAO.getFluxoByName(nomeFluxo);
		if (fluxo != null){
			return fluxo;
		} else {
			throw new IllegalArgumentException("Fluxo n�o encontrado.");
		}
	}	
	
	
	public static Estatistica getUltimaEstatistica(Processo processo) {
		if (processo.getEstatisticaList().size() > 0) {
			return processo.getEstatisticaList().get(processo.getEstatisticaList().size() - 1);
		} 
		return null;
	}
	
	public static RegistraEstatistica instance() {
		return ComponentUtil.getComponent("estatistica");
	}
	
}