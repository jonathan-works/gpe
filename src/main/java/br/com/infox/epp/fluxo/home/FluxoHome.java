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
package br.com.infox.epp.fluxo.home;

import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.home.UsuarioHome;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.HibernateUtil;

 
@Name(FluxoHome.NAME)
public class FluxoHome 
		extends AbstractHome<Fluxo>{
    private static final LogProvider LOG = Logging.getLogProvider(FluxoHome.class);
	private static final long serialVersionUID = 1L;
	public static final String NAME = "fluxoHome";
	
	@In private FluxoManager fluxoManager;
	
	public static FluxoHome instance() {
		return ComponentUtil.getComponent(FluxoHome.NAME);
	}
	
	@Override
	public String persist() {
		try{
		    String persistMessage = super.persist();
	        UsuarioLogin usuarioPublicacao = getInstance().getUsuarioPublicacao();
	        if (usuarioPublicacao != null) {
	            List<Fluxo> usuarioPublicacaoList = usuarioPublicacao
	                    .getFluxoList();
	            if (!usuarioPublicacaoList.contains(instance)) {
	                getEntityManager().refresh(usuarioPublicacao);
	            }
	        }
	        return persistMessage;
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		} 
		return null;	
	}

	public String persistAs() {
		Fluxo fluxo = getInstance();
		HibernateUtil.getSession().evict(fluxo);
		fluxo.setIdFluxo(null);
		return persist();
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
	    Date dataFimPublicacao = getInstance().getDataFimPublicacao();
		if (isValidaDataFimPublicacao(dataFimPublicacao)){
			FacesMessages.instance().add(Severity.ERROR, "#{messages['fluxo.dataPublicacaoErrada']}");
			return false;
		}
		
		return true;
	}

    private boolean isValidaDataFimPublicacao(Date dataFimPublicacao) {
        return dataFimPublicacao != null && dataFimPublicacao.before(getInstance().getDataInicioPublicacao());
    }

	@Override
	public String update() {
		String ret = "";
		try {
			ret = super.update();
		} catch (Exception e) {
		    LOG.error("Erro de restrição: possivelmente um campo foi duplicado.");
		}
		return ret;
	}	
	
	@Override
	public String inactive(Fluxo instance) {
		setInstance(instance);
		if (!fluxoManager.existemProcessosAssociadosAFluxo(instance)) {
			String ret = super.inactive(instance);
			newInstance();
			return ret;
		} else {
			final String message = "#{messages['fluxo.remocaoProibida']}";
			LOG.warn(message);
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, message);
		}
		return null;
	}

    public String importarXPDL(byte[] bytes) {
    	Fluxo fluxo = getInstance();
		try {
			FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
			fluxo.setXml(fluxoXPDL.toJPDL(fluxo.getFluxo()));
		} catch (IllegalXPDLException e) {
			LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage());
		}
		StringBuilder result = new StringBuilder();
		result.append(FluxoHome.NAME).append(".update()");
		return result.toString();
	}
    
//------ Vindo do antigo AbstractFluxoHome
    
    public void setFluxoIdFluxo(Integer id) {
        setId(id);
    }

    public Integer getFluxoIdFluxo() {
        return (Integer) getId();
    }

    @Override
    protected Fluxo createInstance() {
        Fluxo fluxo = new Fluxo();
        UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
                UsuarioHome.NAME, false);
        if (usuarioHome != null) {
            fluxo.setUsuarioPublicacao(usuarioHome.getDefinedInstance());
        }
        return fluxo;
    }

    @Override
    public String remove() {
        UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
                UsuarioHome.NAME, false);
        if (usuarioHome != null) {
            usuarioHome.getInstance().getFluxoList().remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(Fluxo obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }
    
}