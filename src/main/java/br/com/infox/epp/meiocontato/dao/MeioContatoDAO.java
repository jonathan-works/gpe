package br.com.infox.epp.meiocontato.dao;

import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.PARAM_PESSOA;
import static br.com.infox.epp.meiocontato.query.MeioContatoQuery.PARAM_TIPO_MEIO_CONTATO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;

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

    public List<MeioContato> getByPessoaAndTipoMeioContato(Pessoa pessoa, TipoMeioContatoEnum tipoMeioContato) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PESSOA, pessoa);
        parameters.put(PARAM_TIPO_MEIO_CONTATO, tipoMeioContato);
        return getNamedResultList(MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO, parameters);
    }
}
