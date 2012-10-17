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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.ibpm.entity.GrupoModeloDocumento;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.entity.Variavel;
import br.com.itx.util.ComponentUtil;

@Name(ModeloDocumentoHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ModeloDocumentoHome extends AbstractModeloDocumentoHome<ModeloDocumento> {

	private static final String	RAWTYPES	= "rawtypes";
	private static final String	UNCHECKED	= "unchecked";
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

	@SuppressWarnings({ UNCHECKED, RAWTYPES })
	private List<String> getValorVariaveis()	{
		List list = new ArrayList<String>();
		if (getInstance().getTipoModeloDocumento() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("select o.valorVariavel from Variavel o ");
			sb.append("join o.variavelTipoModeloList tipos ");
			sb.append("where tipos.tipoModeloDocumento = :tipo");
			list = getEntityManager().createQuery(sb.toString()).setParameter("tipo", getInstance().getTipoModeloDocumento()).getResultList();
		}
		return list;
	}
	
	@SuppressWarnings({ UNCHECKED, RAWTYPES })
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
	
	public boolean setHistorico(ModeloDocumento oldEntity)	{
		if ( !instance.hasChanges(oldEntity) ) {
			return false;
		}
		if ( oldEntity== null )	{
			return true;
		}
			
		
		HistoricoModeloDocumentoHome home = (HistoricoModeloDocumentoHome)Component.getInstance(HistoricoModeloDocumentoHome.NAME);
		home.newInstance();
		HistoricoModeloDocumento historico = home.getInstance();
		
		historico.setTituloModeloDocumento(oldEntity.getTituloModeloDocumento());
		historico.setDescricaoModeloDocumento(oldEntity.getModeloDocumento());
		historico.setAtivo(oldEntity.getAtivo());
		historico.setDataAlteracao(new Date());
		historico.setModeloDocumento(instance);
		historico.setUsuarioAlteracao((Usuario) ComponentUtil.getComponent(Authenticator.USUARIO_LOGADO));
		
		home.persist();
		return true;
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		Pattern pattern = Pattern.compile("#[{][^{}]+[}]");
		Matcher matcher = pattern.matcher(instance.getModeloDocumento());
		List<String> variaveis = getValorVariaveis();
		boolean eliminarTodos = variaveis.size() == 0;
		
		while(matcher.find())	{
			if (eliminarTodos)	{
				System.out.println(matcher.group());
				FacesMessages.instance().add("Vari�vel "+matcher.group().substring(1)+" n�o cadastrada para este Tipo de Documento");
				return false;
			}
			if(!variaveis.contains(matcher.group()))	{
				System.out.println(matcher.group());
				FacesMessages.instance().add("Vari�vel "+matcher.group().substring(1)+" n�o cadastrada para este Tipo de Documento");
				return false;
			}
		}
		
		if (!setHistorico(getOldEntity()))	{
				return false;
		}
		
		return super.beforePersistOrUpdate();
	}
	
}