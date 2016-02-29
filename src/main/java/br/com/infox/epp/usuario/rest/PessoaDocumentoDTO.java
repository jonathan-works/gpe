package br.com.infox.epp.usuario.rest;

import br.com.infox.epp.pessoa.annotation.Data;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;

import static br.com.infox.epp.usuario.rest.ConstantesDTO.*;

import java.text.SimpleDateFormat;

import javax.validation.constraints.NotNull;

public class PessoaDocumentoDTO {

	@NotNull
	private String documento;
	@NotNull
	@Data(pattern = DATE_PATTERN, past = true)
	private String dataEmissao;
	@NotNull
	private String tipo;
	private String orgaoExpedidor;

	public PessoaDocumentoDTO(PessoaDocumento pessoaDocumento) {
		this.documento = pessoaDocumento.getDocumento();
		this.dataEmissao = new SimpleDateFormat(DATE_PATTERN).format(pessoaDocumento.getDataEmissao());
		this.orgaoExpedidor = pessoaDocumento.getOrgaoEmissor();
		this.tipo = pessoaDocumento.getTipoDocumento().name();
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getOrgaoExpedidor() {
		return orgaoExpedidor;
	}

	public void setOrgaoExpedidor(String orgaoExpedidor) {
		this.orgaoExpedidor = orgaoExpedidor;
	}

}
