package br.com.infox.epp.pessoa.entity;

import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.SEARCH_BY_CPF;
import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.SEARCH_BY_CPF_QUERY;
import static javax.persistence.FetchType.LAZY;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.constants.LengthConstants;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

@Entity
@Table(name = PessoaFisica.TABLE_NAME, uniqueConstraints = { @UniqueConstraint(columnNames = { "nr_cpf" }) })
@PrimaryKeyJoinColumn(name = "id_pessoa_fisica", columnDefinition = "integer")
@NamedQueries({ @NamedQuery(name = SEARCH_BY_CPF, query = SEARCH_BY_CPF_QUERY) })
public class PessoaFisica extends Pessoa {
    public static final String EVENT_LOAD = "evtCarregarPessoaFisica";
    public static final String TABLE_NAME = "tb_pessoa_fisica";
    private static final long serialVersionUID = 1L;

    private String cpf;
    private Date dataNascimento;
    private String certChain;
    private ProcessoDocumentoBin termoAdesao;

    public PessoaFisica() {
        setTipoPessoa(TipoPessoaEnum.F);
    }

    public PessoaFisica(final String cpf, final String nome,
            final Date dataNascimento, final boolean ativo) {
        setTipoPessoa(TipoPessoaEnum.F);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        setNome(nome);
        setAtivo(ativo);
    }

    @Column(name = "nr_cpf", nullable = false, unique = true)
    @Size(max = LengthConstants.NUMERO_CPF)
    @NotNull
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Column(name = "dt_nascimento", nullable = false)
    @NotNull
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
    public String getDataFormatada() {
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
        if (getCpf() == null) {
            if (other.getCpf() != null) {
                return false;
            }
        } else if (!getCpf().equals(other.getCpf())) {
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
        return StringUtil.replaceQuebraLinha(certChain).equals(StringUtil.replaceQuebraLinha(this.certChain));
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_documento_bin", nullable = false)
    @NotNull    
    public ProcessoDocumentoBin getTermoAdesao() {
        return termoAdesao;
    }

    public void setTermoAdesao(ProcessoDocumentoBin termoAdesao) {
        this.termoAdesao = termoAdesao;
    }

}
