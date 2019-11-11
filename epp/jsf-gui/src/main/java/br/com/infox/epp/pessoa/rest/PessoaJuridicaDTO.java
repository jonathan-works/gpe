package br.com.infox.epp.pessoa.rest;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

public class PessoaJuridicaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = LengthConstants.NOME_ATRIBUTO)
    private String nomeFantasia;

    @NotNull
    @Size(max = LengthConstants.NUMERO_RAZAO_SOCIAL)
    private String cnpj;

    @NotNull
    @Size(max = LengthConstants.NOME_PADRAO)
    private String razaoSocial;

    public PessoaJuridicaDTO() {}

    public PessoaJuridicaDTO(String nomeFantasia, String cnpj, String razaoSocial) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public PessoaJuridica toPJ() {
        PessoaJuridica pj = new PessoaJuridica();
        pj.setAtivo(true);
        pj.setCnpj(this.getCnpj());
        pj.setNome(this.getNomeFantasia());
        pj.setRazaoSocial(this.getRazaoSocial());
        pj.setTipoPessoa(TipoPessoaEnum.J);
        return pj;
    }

	@Override
	public String toString() {
		return "PessoaJuridicaDTO [nomeFantasia=" + nomeFantasia + ", cnpj=" + cnpj + ", razaoSocial=" + razaoSocial
				+ "]";
	}

}
