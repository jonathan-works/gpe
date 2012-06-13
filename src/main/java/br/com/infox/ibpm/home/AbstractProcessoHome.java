/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.Component;

import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.itx.component.AbstractHome;


public abstract class AbstractProcessoHome<T> extends AbstractHome<Processo> {

	private static final long serialVersionUID = 1L;

	public void setProcessoIdProcesso(Integer id) {
		setId(id);
	}

	public Integer getProcessoIdProcesso() {
		return (Integer) getId();
	}

	@Override
	protected Processo createInstance() {
		Processo processo = new Processo();
		UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuarioHome != null) {
			processo.setUsuarioCadastroProcesso(usuarioHome
					.getDefinedInstance());
		}
		return processo;
	}

	@Override
	public String remove() {
		UsuarioHome usuario = (UsuarioHome) Component.getInstance(
				"usuarioHome", false);
		if (usuario != null) {
			usuario.getInstance().getProcessoListForIdUsuarioCadastroProcesso()
					.remove(instance);
		}
		return super.remove();
	}

	public String remove(Processo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("processoGrid");
		return ret;
	}

	@Override
	public String persist() {
		String action = super.persist();
		return action;
	}

	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoList();
	}

	/**
	 * Metodo que adiciona o processo passado como parâmetro a lista dos processos
	 * que são conexos ao processo da instância.
	 * @param obj
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcesso(Processo obj, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcesso().add(obj);
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcesso(Processo obj, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcesso().remove(obj);
			refreshGrid(gridId);
		}
	}


	/**
	 * Metodo que adiciona o processo passado como parâmetro a lista dos processos
	 * que o processo da instância é conexo.
	 * @param processo
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcessoConexo().add(processo);
			getEntityManager().flush();
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcessoConexo().remove(processo);
			getEntityManager().flush();
			refreshGrid(gridId);
		}
	}

}