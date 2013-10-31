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
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.query.HistoricoModeloDocumentoQuery;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.list.HistoricoModeloDocumentoList;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(HistoricoModeloDocumentoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class HistoricoModeloDocumentoHome extends AbstractHome<HistoricoModeloDocumento> {

    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE = "/ModeloDocumento/historicoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "historicosModelosDocumento.xls";
    public static final String NAME = "historicoModeloDocumentoHome";
    
    private List<ModeloDocumento> modeloDocumentoList;
    private List<UsuarioLogin> usuarioAlteracaoList;
    private HistoricoModeloDocumento selecionado;
    
	@Override
	public String getTemplate(){
        return HistoricoModeloDocumentoHome.TEMPLATE;
    }
	
	@Override
    public String getDownloadXlsName(){
        return HistoricoModeloDocumentoHome.DOWNLOAD_XLS_NAME;
    }
	
    @Override
    public EntityList<HistoricoModeloDocumento> getBeanList() {
        return HistoricoModeloDocumentoList.instance();
    }
    
    public HistoricoModeloDocumento getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(HistoricoModeloDocumento selecionado) {
        this.selecionado = selecionado;
    }
    
    public void restaurarSelecionado()  {
        if (selecionado==null) {
            return;
        }
        
        ModeloDocumento modelo = selecionado.getModeloDocumento();
        ModeloDocumentoHome modeloHome = (ModeloDocumentoHome)Component.getInstance(ModeloDocumentoHome.NAME);
        
        ModeloDocumento oldEntity = new ModeloDocumento();
        oldEntity.setAtivo(modelo.getAtivo());
        oldEntity.setIdModeloDocumento(modelo.getIdModeloDocumento());
        oldEntity.setModeloDocumento(modelo.getModeloDocumento());
        oldEntity.setTipoModeloDocumento(modelo.getTipoModeloDocumento());
        oldEntity.setTituloModeloDocumento(modelo.getTituloModeloDocumento());
        
        modeloHome.newInstance();
        modeloHome.setInstance(modelo);
        modeloHome.setOldEntity(oldEntity);
        modelo.setTituloModeloDocumento(selecionado.getTituloModeloDocumento());
        modelo.setModeloDocumento(selecionado.getDescricaoModeloDocumento());
        modelo.setAtivo(selecionado.getAtivo());
        
        modeloHome.update();
    }

    @SuppressWarnings(WarningConstants.UNCHECKED)
    @Override
    public void create() {
        super.create();
        javax.persistence.Query query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_MODELO_QUERY);
        setModeloDocumentoList(query.getResultList());
        query = EntityUtil.createQuery(HistoricoModeloDocumentoQuery.LIST_USUARIO_QUERY);
        query.setParameter(HistoricoModeloDocumentoQuery.LIST_USUARIO_PARAM_MODELO, ((ModeloDocumentoHome)Component.getInstance(ModeloDocumentoHome.NAME)).getInstance());
        setUsuarioAlteracaoList(query.getResultList());
    }
    
    public List<UsuarioLogin> getUsuarioAlteracaoList() {
        return usuarioAlteracaoList;
    }

    public void setUsuarioAlteracaoList(List<UsuarioLogin> usuarioAlteracaoList) {
        this.usuarioAlteracaoList = usuarioAlteracaoList;
    }
    
    public List<ModeloDocumento> getModeloDocumentoList() {
        return modeloDocumentoList;
    }

    public void setModeloDocumentoList(
            List<ModeloDocumento> historicoModeloDocumentoList) {
        this.modeloDocumentoList = historicoModeloDocumentoList;
    }
	
}