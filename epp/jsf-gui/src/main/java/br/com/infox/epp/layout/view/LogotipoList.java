package br.com.infox.epp.layout.view;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.layout.entity.ResourceBin;

@Named
@ViewScoped
public class LogotipoList extends DataList<ResourceBin> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getDefaultOrder() {
		return null;
	}

	@Override
	protected String getDefaultEjbql() {
		return "from Resources";
	}

	public String getTextoSkins(ResourceBin resource) {
		/*if (logotipo.getSkinsAssociadas() == null) {
			return null;
		}
		String retorno = "";
		Iterator<Skin> it = logotipo.getSkinsAssociadas().iterator();
		while (it.hasNext()) {
			if (it.hasNext()) {
				retorno += ", ";
			}
			Skin skin = it.next();
			retorno += skin.getNome();
		}
		return retorno;*/
		throw new UnsupportedOperationException();
	}

}
