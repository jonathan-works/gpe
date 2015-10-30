package br.com.infox.epp.meiocontato.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.meiocontato.dao.MeioContatoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(MeioContatoManager.NAME)
@Stateless
public class MeioContatoManager extends Manager<MeioContatoDAO, MeioContato>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "meioContatoManager";
	
	public List<MeioContato> getByPessoa(Pessoa pessoa) {
		return getDao().getByPessoa(pessoa);
	}
	
	public MeioContato getMeioContatoTelefoneFixoByPessoa(Pessoa pessoa){
		return getMeioContatoByPessoaAndTipo(pessoa, TipoMeioContatoEnum.TF);
	}
	
	public MeioContato getMeioContatoTelefoneMovelByPessoa(Pessoa pessoa){
		return getMeioContatoByPessoaAndTipo(pessoa, TipoMeioContatoEnum.TM);
	}
	
	public MeioContato getMeioContatoByPessoaAndTipo(Pessoa pessoa, TipoMeioContatoEnum tipoMeioContatoEnum){
		return getDao().getMeioContatoByPessoaAndTipo(pessoa, tipoMeioContatoEnum);
	}
	
	public MeioContato createMeioContatoTelefoneFixo(String vlMeioContato, Pessoa pessoa){
		return createMeioContato(vlMeioContato, pessoa, TipoMeioContatoEnum.TF);
	}
	
	public MeioContato createMeioContatoTelefoneMovel(String vlMeioContato, Pessoa pessoa){
		return createMeioContato(vlMeioContato, pessoa, TipoMeioContatoEnum.TM);
	}
	
	public MeioContato createMeioContatoEmail(String vlMeioContato, Pessoa pessoa){
		return createMeioContato(vlMeioContato, pessoa, TipoMeioContatoEnum.EM);
	}
	
	public List<MeioContato> getByPessoaAndTipoMeioContato(Pessoa pessoa, TipoMeioContatoEnum tipoMeioContato) {
	    return getDao().getByPessoaAndTipoMeioContato(pessoa, tipoMeioContato);
	}
	
	 public boolean existeMeioContatoByPessoaTipoValor(Pessoa pessoa, TipoMeioContatoEnum tipo, String valor){
        return getDao().existeMeioContatoByPessoaTipoValor(pessoa, tipo, valor);
    }
	
	public MeioContato createMeioContato(String vlMeioContato, Pessoa pessoa, TipoMeioContatoEnum tipoMeioContato){
		MeioContato meioContato = new MeioContato();
		meioContato.setMeioContato(vlMeioContato);
		meioContato.setPessoa(pessoa);
		meioContato.setTipoMeioContato(tipoMeioContato);
		return meioContato;
	}
	
	public void removeMeioContatoByPessoa(Pessoa pessoa) throws DAOException {
	    List<MeioContato> meioContatoList = getByPessoa(pessoa);
	    for (MeioContato meioContato : meioContatoList) {
            getDao().remove(meioContato);
        }
	}
	
	public boolean isTelefoneMovel(String numeroTelefone) {
		Integer valorInicial = Integer.valueOf(numeroTelefone.substring(0, 1));
		return valorInicial >= 6 && valorInicial <= 9;
	}
}