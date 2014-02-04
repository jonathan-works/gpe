package br.com.infox.epp.access.manager;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.access.dao.PapelDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Name(PapelManager.NAME)
@AutoCreate
public class PapelManager extends Manager<PapelDAO, Papel> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "papelManager";
	
	public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento){
		return getDao().getPapeisNaoAssociadosATipoModeloDocumento(tipoModeloDocumento);
	}
	
	public List<Papel> getPapeisNaoAssociadosATipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento){
		return getDao().getPapeisNaoAssociadosATipoProcessoDocumento(tipoProcessoDocumento);
	}
	
	public Papel getPapelByIdentificador(String identificador){
		return getDao().getPapelByIndentificador(identificador);
	}
	
	public List<Papel> getPapeisByListaDeIdentificadores(List<String> identificadores){
		if(identificadores == null || identificadores.isEmpty()) {
			return new ArrayList<Papel>();
		}
		return getDao().getPapeisByListaDeIdentificadores(identificadores);
	}
	public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao){
		return getDao().getPapeisDeUsuarioByLocalizacao(localizacao);
	}

}
