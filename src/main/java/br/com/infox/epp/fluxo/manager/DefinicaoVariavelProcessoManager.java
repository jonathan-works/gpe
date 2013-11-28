package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.dao.DefinicaoVariavelProcessoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;

@Name(DefinicaoVariavelProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DefinicaoVariavelProcessoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "definicaoVariavelProcessoManager";
	public static final String JBPM_VARIABLE_TYPE = "processo";
	
	@In
	private DefinicaoVariavelProcessoDAO definicaoVariavelProcessoDAO;
	
	public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
		return definicaoVariavelProcessoDAO.listVariaveisByFluxo(fluxo);
	}
	
	public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo, int start, int count) {
		return definicaoVariavelProcessoDAO.listVariaveisByFluxo(fluxo, start, count);
	}
	
	public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
		return definicaoVariavelProcessoDAO.getTotalVariaveisByFluxo(fluxo);
	}
	
	public String getNomeAmigavel(DefinicaoVariavelProcesso variavelProcesso) {
		if (variavelProcesso == null || variavelProcesso.getNome() == null) {
			return null;
		}
		String[] nome = variavelProcesso.getNome().split(":");
		if (nome.length == 1) {
			return nome[0];
		}
		return nome[1];
	}
	
	public void setNome(DefinicaoVariavelProcesso variavelProcesso, String nomeAmigavel) {
		String nome = nomeAmigavel.replace(' ', '_').replace('/', '_');
		variavelProcesso.setNome(JBPM_VARIABLE_TYPE + ":" + nome);
	}
}
