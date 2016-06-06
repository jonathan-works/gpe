package br.com.infox.epp.pessoa.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.jsf.converter.CnpjConverter;
import br.com.infox.jsf.converter.CpfConverter;

@Entity
@Table(name = Pessoa.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_pessoa";

    @Id 
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name = "PessoaGenerator", sequenceName = "sq_tb_pessoa") 
    @GeneratedValue(generator = "PessoaGenerator", strategy = GenerationType.SEQUENCE) 
    @Column(name = "id_pessoa", unique = true, nullable = false) 
    private Integer idPessoa;

    @Enumerated(EnumType.STRING) 
    @Column(name = "tp_pessoa", nullable = false, columnDefinition = "varchar(1)", length = LengthConstants.FLAG) 
    private TipoPessoaEnum tipoPessoa;

    @NotNull @Size(max = LengthConstants.NOME_ATRIBUTO) 
    @Column(name = "nm_pessoa", nullable = false, length = LengthConstants.NOME_ATRIBUTO) 
    private String nome;

    @Column(name = "in_ativo", nullable = false) 
    private Boolean ativo = Boolean.TRUE;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pessoa") 
    private List<MeioContato> meioContatoList = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pessoa")
    private List<PessoaDocumento> pessoaDocumentoList = new ArrayList<>();
    
    public Integer getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public TipoPessoaEnum getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<MeioContato> getMeioContatoList() {
        return meioContatoList;
    }

    public void setMeioContatoList(List<MeioContato> meioContatoList) {
        this.meioContatoList = meioContatoList;
    }

    @Transient
    public abstract String getCodigo();

    @Transient
    public String getCodigoFormatado() {
        if (tipoPessoa == TipoPessoaEnum.F) {
            return CpfConverter.format(getCodigo());
        } else if (tipoPessoa == TipoPessoaEnum.J) {
            return CnpjConverter.format(getCodigo());
        }
        return getCodigo();
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getIdPessoa() == null) ? 0 : getIdPessoa().hashCode());
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
        if (!(obj instanceof Pessoa)) {
            return false;
        }
        Pessoa other = (Pessoa) obj;
        if (getIdPessoa() == null) {
            if (other.getIdPessoa() != null) {
                return false;
            }
        } else if (!getIdPessoa().equals(other.getIdPessoa())) {
            return false;
        }
        return true;
    }

    @Transient
    public MeioContato getMeioContato(TipoMeioContatoEnum meioContatoEnum) {
        for (MeioContato meioContato : meioContatoList) {
            if (meioContato.getTipoMeioContato() == meioContatoEnum) {
                return meioContato;
            }
        }
        return null;
    }

    @Transient
    public MeioContato getTelefoneFixo() {
        return getMeioContato(TipoMeioContatoEnum.TF);
    }

    @Transient
    public MeioContato getTelefoneMovel() {
        return getMeioContato(TipoMeioContatoEnum.TM);
    }

	public List<PessoaDocumento> getPessoaDocumentoList() {
		return pessoaDocumentoList;
	}

	public void setPessoaDocumentoList(List<PessoaDocumento> pessoaDocumentoList) {
		this.pessoaDocumentoList = pessoaDocumentoList;
	}

}
