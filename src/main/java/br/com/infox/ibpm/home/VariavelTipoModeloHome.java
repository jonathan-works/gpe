package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.ibpm.entity.TipoModeloDocumento;
import br.com.infox.ibpm.entity.Variavel;
import br.com.infox.ibpm.entity.VariavelTipoModelo;
import br.com.itx.util.EntityUtil;

@Name("variavelTipoModeloHome")
@BypassInterceptors
public class VariavelTipoModeloHome extends AbstractVariavelTipoModeloHome<VariavelTipoModelo> {

	private static final long serialVersionUID = 1L;
	
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
}