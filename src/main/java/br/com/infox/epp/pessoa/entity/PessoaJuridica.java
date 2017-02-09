package br.com.infox.epp.pessoa.entity;

import static br.com.infox.epp.pessoa.query.PessoaJuridicaQuery.SEARCH_BY_CNPJ;
import static br.com.infox.epp.pessoa.query.PessoaJuridicaQuery.SEARCH_BY_CNPJ_QUERY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.hibernate.util.HibernateUtil;

@Entity
@Table(name = PessoaJuridica.TABLE_NAME)
@PrimaryKeyJoinColumn(name = "id_pessoa_juridica", columnDefinition = "integer")
@NamedQueries({ @NamedQuery(name = SEARCH_BY_CNPJ, query = SEARCH_BY_CNPJ_QUERY) })
public class PessoaJuridica extends Pessoa {

    public static final String TABLE_NAME = "tb_pessoa_juridica";
    public static final String EVENT_LOAD = "evtCarregarPessoaJuridica";
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = LengthConstants.NUMERO_RAZAO_SOCIAL)
    @Column(name = "nr_cnpj", nullable = false, unique = true, length = LengthConstants.NUMERO_RAZAO_SOCIAL)
    private String cnpj;
    
    @NotNull
    @Size(max = LengthConstants.NOME_PADRAO)
    @Column(name = "nm_razao_social", nullable = false, length = LengthConstants.NOME_PADRAO)
    private String razaoSocial;

    public PessoaJuridica() {
        setTipoPessoa(TipoPessoaEnum.J);
    }
    
    public PessoaJuridica(String cnpj, String razaoSocial, String nome, Boolean ativo) {
        super();
        setNome(nome);
        setAtivo(ativo);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cnpj == null) ? 0 : cnpj.hashCode());
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
        obj = HibernateUtil.removeProxy(obj);        
        if (!(obj instanceof PessoaJuridica)) {
            return false;
        }
        PessoaJuridica other = (PessoaJuridica) obj;
        if (cnpj == null) {
            if (other.cnpj != null) {
                return false;
            }
        } else if (!cnpj.equals(other.cnpj)) {
            return false;
        }
        return true;
    }

    @Override
    @Transient
    public String getCodigo() {
        return getCnpj();
    }
}
