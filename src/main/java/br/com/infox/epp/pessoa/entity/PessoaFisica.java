package br.com.infox.epp.pessoa.entity;

import static javax.persistence.FetchType.LAZY;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.itx.util.StringUtil;

@Entity
@Table(schema="public", name=PessoaFisica.TABLE_NAME)
@PrimaryKeyJoinColumn(name="id_pessoa_fisica", columnDefinition = "integer")
public class PessoaFisica extends Pessoa {
    public static final String EVENT_LOAD = "evtCarregarPessoaFisica";
	public static final String TABLE_NAME = "tb_pessoa_fisica";
	private static final long serialVersionUID = 1L;
	
	private String cpf;
	private Date dataNascimento;
    private String certChain;
	
	public PessoaFisica(){
		setTipoPessoa(TipoPessoaEnum.F);
	}
	
	@Column(name="nr_cpf", nullable=false, unique=true)
	@Size(max=LengthConstants.NUMERO_CPF)
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@Column(name="dt_nascimento", nullable=false)
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
    @Column(name = "ds_cert_chain")
    @Basic(fetch = LAZY)
    public String getCertChain() {
        return certChain;
    }
    
    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }
	
	@Transient
	public String getDataFormatada(){
		return DateFormat.getDateInstance().format(dataNascimento);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
		    return false;
		}
		if (!(obj instanceof PessoaFisica)) {
		    return false;
		}
		PessoaFisica other = (PessoaFisica) obj;
		if (cpf == null) {
			if (other.cpf != null) {
			    return false;
			}
		} else if (!cpf.equals(other.cpf)) {
		    return false;
		}
		return true;
	}

    @Override
    @Transient
    public String getCodigo() {
        return getCpf();
    }
    
    public boolean checkCertChain(String certChain) {
        if (certChain == null) {
            throw new IllegalArgumentException("O parâmetro não deve ser nulo");
        } 
        return StringUtil.replaceQuebraLinha(certChain).equals(
                StringUtil.replaceQuebraLinha(this.certChain));
    }

}
