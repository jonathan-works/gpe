package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Variavel;
import br.com.infox.ibpm.entity.VariavelTipoModelo;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(VariavelTipoModeloHome.NAME)
public class VariavelTipoModeloHome extends AbstractHome<VariavelTipoModelo> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "variavelTipoModeloHome";
	
	public void addVariavelTipoModelo(Variavel obj) {
		if (getInstance() != null) {
			getInstance().setVariavel(obj);
			
			VariavelTipoModelo variavelTipoModelo = getInstance();
			
			persist();
			
			VariavelHome.instance().getInstance().getVariavelTipoModeloList().add(variavelTipoModelo);
			
			FacesMessages.instance().clear();
		}
	}
	
	public void removeVariavelTipoModelo(VariavelTipoModelo obj) {
		if (getInstance() != null) {

			Variavel variavel = obj.getVariavel();
			
			List<VariavelTipoModelo> variavelTipoModeloList = variavel.getVariavelTipoModeloList();
			variavelTipoModeloList.remove(obj);
			
			getEntityManager().remove(obj);
			
			getEntityManager().flush();
			EntityUtil.flush(getEntityManager());
			FacesMessages.instance().add(Severity.INFO, "Excluido com Sucesso");

			newInstance();
			FacesMessages.instance().clear();
		}
	}	
	
	public void addTipoModeloVariavel(TipoModeloDocumento obj) {
		if (getInstance() != null) {
			getInstance().setTipoModeloDocumento(obj);
			
			VariavelTipoModelo variavelTipoModelo = getInstance();
			
			persist();
			
			TipoModeloDocumentoHome.instance().getInstance().getVariavelTipoModeloList().add(variavelTipoModelo);
			
			FacesMessages.instance().clear();
		}
	}
	
	public void removeTipoModeloVariavel(VariavelTipoModelo obj) {
		if (getInstance() != null) {
			TipoModeloDocumento tipoModeloDocumento = obj.getTipoModeloDocumento();
			
			List<VariavelTipoModelo> variavelTipoModeloList = tipoModeloDocumento.getVariavelTipoModeloList();
			variavelTipoModeloList.remove(obj);
			
			getEntityManager().remove(obj);
			
			getEntityManager().flush();
			EntityUtil.flush(getEntityManager());
			FacesMessages.instance().add(Severity.INFO, "Excluido com Sucesso");

			newInstance();
			FacesMessages.instance().clear();
		}
	}	
	
	public void setVariavelTipoModeloIdVariavelTipoModelo(Integer id) {
        setId(id);
    }

    public Integer getVariavelTipoModeloIdVariavelTipoModelo() {
        return (Integer) getId();
    }
    
    @Override
    protected VariavelTipoModelo createInstance() {
        VariavelTipoModelo variavelTipoModelo = new VariavelTipoModelo();
        
        VariavelHome variavelHome = VariavelHome.instance();
        if (variavelHome != null) {
            variavelTipoModelo.setVariavel(variavelHome.getDefinedInstance());
        }
        
        TipoModeloDocumentoHome tipoModeloDocumentoHome = TipoModeloDocumentoHome.instance();
        if (tipoModeloDocumentoHome != null) {
            variavelTipoModelo.setTipoModeloDocumento(tipoModeloDocumentoHome.getDefinedInstance());
        }       
        return variavelTipoModelo;
    }

    @Override
    public String remove() {
        VariavelHome variavel = VariavelHome.instance();
        if (variavel != null) {
            variavel.getInstance().getVariavelTipoModeloList().remove(instance);
        }
        TipoModeloDocumentoHome tipoModeloDocumento = TipoModeloDocumentoHome.instance();
        if (tipoModeloDocumento != null) {
            tipoModeloDocumento.getInstance().getVariavelTipoModeloList().remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(VariavelTipoModelo obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    @Override
    public String persist() {
       String action = super.persist();
       newInstance();
       return action;
    }

}