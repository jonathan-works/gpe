package br.com.infox.epp.documento.modelo;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.endereco.Endereco;
import br.com.infox.epp.endereco.PessoaEndereco;
import br.com.infox.epp.endereco.PessoaEnderecoSearch;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.dao.MetadadoProcessoDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.status.dao.StatusProcessoDao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloDocumentoFolhaRostoSearch extends PersistenceController {
	
	@Inject
    private MetadadoProcessoDAO metadadoProcessoDAO;
	@Inject
	private StatusProcessoDao statusProcessoDao;
	@Inject
	private PessoaEnderecoSearch pessoaEnderecoSearch;
	@Inject
	private MeioContatoManager meioContatoManager;

	public String gerarTextoModeloDocumento(Processo processo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"border-style: solid;\">");
		sb.append("<center><strong>Dados do processo</strong></center>");
		sb.append("<table style=\"width: 100%; border: none;\">");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<strong>Número: </strong>");
		sb.append(processo.getNumeroProcesso());
		sb.append("</td>");
		sb.append("<td>");
		sb.append("<strong>Data de protocolo: </strong>");
		sb.append(getDataFormatada(processo.getDataInicio()));
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<strong>Situação: </strong>");
		sb.append(getStatusProcesso(processo));
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<strong>Origem: </strong>");
		sb.append(processo.getLocalizacao().getCaminhoCompletoFormatado());
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</div>");
		
		for(ParticipanteProcesso participanteProcesso : processo.getParticipantes()) {
			PessoaEndereco pessoaEndereco = pessoaEnderecoSearch.getByPessoa(participanteProcesso.getPessoa());
			sb.append("<div style=\"border-style: solid; margin-top: 15px;\">");
			sb.append("<center><strong>Interessado</strong></center>");
			sb.append("<table style=\"width: 100%; border: none;\">");
			sb.append("<tr><td><strong>Nome: </strong>");
			sb.append(participanteProcesso.getNome());
			sb.append("</td></tr>");
			sb.append("<tr><td><strong>CPF / CNPJ: </strong>");
			sb.append(participanteProcesso.getPessoa().getCodigo());
			sb.append("</td></tr>");
			
			if(pessoaEndereco != null) {
				Endereco endereco = pessoaEndereco.getEndereco();
				sb.append("<tr><td><strong>Logradouro: </strong>");
				sb.append(endereco.getLogradouro());
				sb.append("</td></tr>");
				sb.append("<tr><td><strong>Número: </strong>");
				sb.append(endereco.getNumero());
				sb.append("</td></tr>");
				sb.append("<tr><td><strong>Complemento: </strong>");
				sb.append(endereco.getComplemento());
				sb.append("</td></tr>");
				sb.append("<tr><td><strong>Bairro: </strong>");
				sb.append(endereco.getBairro());
				sb.append("</td></tr>");
				
				sb.append("<tr>");
				sb.append("<td><strong>Cidade: </strong>");
				sb.append(endereco.getMunicipio().getNome());
				sb.append("</td>");
				sb.append("<td><strong>UF: </strong>");
				sb.append(endereco.getMunicipio().getEstado().getSigla());
				sb.append("</td>");
				sb.append("<td><strong>CEP: </strong>");
				sb.append(endereco.getCep());
				sb.append("</td>");
				sb.append("</tr>");
			}
			
			sb.append("<tr><td><strong>Telefone(s): </strong>");
			sb.append(getTelefones(participanteProcesso.getPessoa()));
			sb.append("</td></tr>");
			
			sb.append("</table>");
			sb.append("</div>");	
		}
		
    	return sb.toString();
    }
	
	private String getDataFormatada(Date data) {
		return DateUtil.formatarData(data, "dd/MM/yyyy");
	}
	
	private String getTelefones(Pessoa pessoa) {
		StringBuilder sb = new StringBuilder();
		MeioContato telefoneFixo = meioContatoManager.getMeioContatoTelefoneFixoByPessoa(pessoa);
		MeioContato telefoneMovel = meioContatoManager.getMeioContatoTelefoneMovelByPessoa(pessoa);
		
		if(telefoneFixo != null) {
			sb.append(telefoneFixo.getMeioContato());
		}
		
		if(telefoneMovel != null) {
			if(telefoneFixo != null) {
				sb.append(" / ");
			}
			sb.append(telefoneMovel.getMeioContato());
		}
		return sb.toString();
	}
	
	private String getStatusProcesso(Processo processo) {
		if(processo != null) {
			List<MetadadoProcesso> listaStatusProcesso = metadadoProcessoDAO.getMetadadoProcessoByType(processo, "statusProcesso");
			if(!listaStatusProcesso.isEmpty()) {
				return statusProcessoDao.find(Integer.valueOf(listaStatusProcesso.get(0).getValor())).getDescricao();
			}
		}
		return "";
	}
}
