package br.com.infox.ibpm.home;

import java.util.Date;

import javax.xml.bind.ValidationException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.epa.manager.ParteProcessoManager;
import br.com.infox.epa.manager.ProcessoEpaManager;
import br.com.infox.ibpm.entity.HistoricoParteProcesso;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.itx.component.AbstractHome;

@Name(HistoricoParteProcessoHome.NAME)
public class HistoricoParteProcessoHome extends AbstractHome<HistoricoParteProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoParteProcessoHome";
	
	@In private ParteProcessoManager parteProcessoManager;
	
	public void restaurar(String motivoRestauracao){
		try {
			HistoricoParteProcesso historicoDeRestauracao = parteProcessoManager
					.restaurarParteProcesso(getParteProcessoAtual(), getInstance(), motivoRestauracao);
			setInstance(historicoDeRestauracao);
		} catch (ValidationException ve) {
			ve.printStackTrace();
		}
	}
	
	private ParteProcesso getParteProcessoAtual(){
		return getInstance().getParteModificada();
	}

	@Override
	protected boolean beforePersistOrUpdate() {
		instance.setResponsavelPorModificacao(Authenticator.getUsuarioLogado());
		instance.setDataModificacao(new Date());
		instance.setAtivo(instance.getParteModificada().getAtivo());
		return true;
	}

	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		return ret;
	}
	
	
	
}