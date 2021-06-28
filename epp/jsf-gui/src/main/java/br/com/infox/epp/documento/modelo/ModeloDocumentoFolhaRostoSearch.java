package br.com.infox.epp.documento.modelo;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
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
import br.com.infox.ibpm.task.manager.TaskInstanceManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloDocumentoFolhaRostoSearch extends PersistenceController {
	
	private static final String VARIAVEL_DESCRICAO_GERAL_PROCESSO = "descricaoGeralProcesso";
	
	@Inject
    private MetadadoProcessoDAO metadadoProcessoDAO;
	@Inject
	private StatusProcessoDao statusProcessoDao;
	@Inject
	private PessoaEnderecoSearch pessoaEnderecoSearch;
	@Inject
	private MeioContatoManager meioContatoManager;
	@Inject
	private TaskInstanceManager taskInstanceManager;

	public String gerarTextoModeloDocumento(Processo processo) {
		ProcessInstance processInstance = getEntityManager().find(ProcessInstance.class, processo.getIdJbpm());
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"border-style: solid; border-width: thin;\">");
		sb.append("<center><strong>Dados do processo</strong></center>");
		sb.append("<table style=\"width: 100%; border: none;\">");
		sb.append("<tr>");
		sb.append("<td style=\"width: 75%;\">");
		sb.append("<strong>Número: </strong>");
		sb.append(processo.getNumeroProcesso());
		sb.append("</td>");
		sb.append("<td style=\"font-size:12px;width: 25%;\">");
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
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<strong>Assunto: </strong>");
		sb.append(processInstance.getProcessDefinition().getName());
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("<strong>Subassunto: </strong>");
		sb.append(taskInstanceManager.getTaskInstanceOpen(processo).getTask().getName());
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</div>");
		
		for(ParticipanteProcesso participanteProcesso : processo.getParticipantes()) {
			PessoaEndereco pessoaEndereco = pessoaEnderecoSearch.getByPessoa(participanteProcesso.getPessoa());
			sb.append("<div style=\"border-style: solid; border-width: thin; margin-top: 15px;\">");
			sb.append("<center><strong>Interessado</strong></center>");
			sb.append("<table style=\"width: 100%; border: none;\">");
			sb.append("<tr><td><strong>Nome: </strong>");
			sb.append(participanteProcesso.getNome());
			sb.append("</td></tr>");
			sb.append("<tr><td><strong>CPF / CNPJ: </strong>");
			sb.append(participanteProcesso.getPessoa().getCodigoFormatado());
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
				sb.append("<td style=\"width: 60%;\"><strong>Cidade: </strong>");
				sb.append(endereco.getMunicipio().getNome());
				sb.append("</td>");
				sb.append("<td style=\"width: 10%;\"><strong>UF: </strong>");
				sb.append(endereco.getMunicipio().getEstado().getSigla());
				sb.append("</td>");
				sb.append("<td style=\"width: 20%;\"><strong>CEP: </strong>");
				sb.append(endereco.getCep());
				sb.append("</td>");
				sb.append("</tr>");
			}
			
			String telefones = getTelefones(participanteProcesso.getPessoa());
			if(!StringUtil.isEmpty(telefones)) {
				sb.append("<tr><td><strong>Telefone(s): </strong>");
				sb.append(telefones);
				sb.append("</td></tr>");
			}
			
			sb.append("</table>");
			sb.append("</div>");	
		}
		
		String descricaoProcesso = getDescricaoGeralProcesso(processInstance);
		if(!StringUtil.isEmpty(descricaoProcesso)) {
			sb.append("<div style=\"border-style: solid; border-width: thin; margin-top: 15px;\">");
			sb.append("<center><strong>Descrição do Processo</strong></center>");
			sb.append(descricaoProcesso);
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
	
	private String getDescricaoGeralProcesso(ProcessInstance processInstance) {
		if(processInstance.getContextInstance().getVariable(VARIAVEL_DESCRICAO_GERAL_PROCESSO) != null && processInstance.getContextInstance().getVariable(VARIAVEL_DESCRICAO_GERAL_PROCESSO) instanceof String) {
			return processInstance.getContextInstance().getVariable(VARIAVEL_DESCRICAO_GERAL_PROCESSO).toString();
		}
		return "";
	}
}
