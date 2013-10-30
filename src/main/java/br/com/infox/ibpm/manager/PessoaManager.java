package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.PessoaFisicaDAO;
import br.com.infox.ibpm.dao.PessoaJuridicaDAO;
import br.com.infox.ibpm.entity.PessoaFisica;

@Name(PessoaManager.NAME)
@AutoCreate
public class PessoaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaManager";
	
	@In private PessoaFisicaDAO pessoaFisicaDAO;
	@In private PessoaJuridicaDAO pessoaJuridicaDAO;
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		if (tipoPessoa.equals("F") || tipoPessoa.equals("f")) {
			Events.instance().raiseEvent(PessoaFisica.EVENT_LOAD, pessoaFisicaDAO.searchByCpf(codigo));
		} else if (tipoPessoa.equals("J") || tipoPessoa.equals("j")){
			Events.instance().raiseEvent("evtCarregarPessoaJuridica", pessoaJuridicaDAO.searchByCnpj(codigo));
		} else {
		    return;
		}
	}
	
	public PessoaFisica getPessoaFisicaByCpf(String cpf){
		return pessoaFisicaDAO.searchByCpf(cpf);
	}

}
