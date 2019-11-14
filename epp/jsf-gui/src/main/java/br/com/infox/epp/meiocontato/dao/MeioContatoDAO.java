package br.com.infox.epp.meiocontato.dao;

import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.PARAM_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.PARAM_TIPO_CONTATO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.PARAM_VALOR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Stateless
@AutoCreate
@Name(MeioContatoDAO.NAME)
public class MeioContatoDAO extends DAO<MeioContato> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "meioContatoDAO";
	
	public List<MeioContato> getByPessoa(Pessoa pessoa) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PESSOA, pessoa);
		return getNamedResultList(MEIO_CONTATO_BY_PESSOA, parameters);
	}
	
	public MeioContato getMeioContatoByPessoaAndTipo(Pessoa pessoa, TipoMeioContatoEnum tipoMeioContatoEnum){
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(PARAM_PESSOA, pessoa);
		parameters.put(PARAM_TIPO_CONTATO, tipoMeioContatoEnum);
		return getNamedSingleResult(MEIO_CONTATO_BY_PESSOA_AND_TIPO, parameters);
	}

    public List<MeioContato> getByPessoaAndTipoMeioContato(Pessoa pessoa, TipoMeioContatoEnum tipoMeioContato) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PESSOA, pessoa);
        parameters.put(PARAM_TIPO_CONTATO, tipoMeioContato);
        return getNamedResultList(MEIO_CONTATO_BY_PESSOA_AND_TIPO, parameters);
    }
    
    public boolean existeMeioContatoByPessoaTipoValor(Pessoa pessoa, TipoMeioContatoEnum tipo, String valor){
    	Map<String, Object> parameters = new HashMap<>(3);
        parameters.put(PARAM_PESSOA, pessoa);
        parameters.put(PARAM_TIPO_CONTATO, tipo);
        parameters.put(PARAM_VALOR, valor);
        return (Long) getNamedSingleResult(EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR, parameters) > 0;
    }
    
    public void adicionaMeioContatos(List<MeioContato> contatos) {
		if (contatos != null && !contatos.isEmpty()) {
	    	for (MeioContato meioContato : contatos) {
				getEntityManager().persist(meioContato);
			}
			getEntityManager().flush();
		}
	}
}
