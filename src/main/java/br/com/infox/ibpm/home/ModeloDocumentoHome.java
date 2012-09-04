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
package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.entity.Variavel;
import br.com.itx.util.ComponentUtil;

@Name(ModeloDocumentoHome.NAME)
@BypassInterceptors
public class ModeloDocumentoHome extends AbstractModeloDocumentoHome<ModeloDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloDocumentoHome";
	
	private GrupoModeloDocumento grupoModeloDocumento;
	private TipoModeloDocumento tipoModeloDocumento;
	 
	@Override
	public void setId(Object id) {
		boolean changed = id != null && !id.equals(getId());
		super.setId(id);
		if (changed) {
			tipoModeloDocumento = getInstance().getTipoModeloDocumento();
			grupoModeloDocumento = tipoModeloDocumento.getGrupoModeloDocumento();
		}
		if (id == null) {
			tipoModeloDocumento = null;
			grupoModeloDocumento = null;
		} 
	}
	
	public GrupoModeloDocumento getGrupoModeloDocumento() {
		return grupoModeloDocumento;
	}
	
	public void setGrupoModeloDocumento(
			GrupoModeloDocumento grupoModeloDocumento) {
		this.grupoModeloDocumento = grupoModeloDocumento;
	}
	
	public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
		this.tipoModeloDocumento = tipoModeloDocumento;
	}
	
	public TipoModeloDocumento getTipoModeloDocumento() {
		return tipoModeloDocumento;
	}

	@SuppressWarnings("unchecked")
	public List<Variavel> getVariaveis() {
		List list = new ArrayList<Variavel>();
		if (getInstance().getTipoModeloDocumento() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("select o from Variavel o ");
			sb.append("join o.variavelTipoModeloList tipos ");
			sb.append("where tipos.tipoModeloDocumento = :tipo");
			list = getEntityManager().createQuery(sb.toString()).setParameter("tipo", getInstance().getTipoModeloDocumento()).getResultList();
		}
		return list;
	}

	@Override
	protected boolean beforePersistOrUpdate() {
		HistoricoModeloDocumentoHome home = new HistoricoModeloDocumentoHome();
		home.newInstance();
		HistoricoModeloDocumento historico = home.getInstance();
		
		ModeloDocumento oldEntity = getOldEntity();
		historico.setAtivo(oldEntity.getAtivo());
		historico.setTituloModeloDocumento(oldEntity.getTituloModeloDocumento());
		historico.setDescricaoModeloDocumento(oldEntity.getModeloDocumento());
		historico.setDataAlteracao(new Date());
		historico.setModeloDocumento(instance);
		historico.setUsuarioAlteracao((Usuario) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
		//home.update();
		getEntityManager().persist(historico);
		
		return super.beforePersistOrUpdate();
	}
	
}