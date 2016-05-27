package br.com.infox.epp.processo.documento.entity;

import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_NOME;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_NOME_QUERY;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO_AND_DESCRICAO;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO_AND_DESCRICAO_QUERY;
import static br.com.infox.epp.processo.documento.query.PastaQuery.GET_BY_PROCESSO_QUERY;
import static br.com.infox.epp.processo.documento.query.PastaQuery.TOTAL_DOCUMENTOS_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaQuery.TOTAL_DOCUMENTOS_PASTA_QUERY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name = Pasta.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = GET_BY_PROCESSO, query = GET_BY_PROCESSO_QUERY),
    @NamedQuery(name = TOTAL_DOCUMENTOS_PASTA, query = TOTAL_DOCUMENTOS_PASTA_QUERY),
    @NamedQuery(name = GET_BY_PROCESSO_AND_DESCRICAO, query = GET_BY_PROCESSO_AND_DESCRICAO_QUERY),
    @NamedQuery(name = GET_BY_NOME, query = GET_BY_NOME_QUERY)
})
public class Pasta implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_pasta";
    private static final String SEQUENCE_NAME = "sq_pasta";
    private static final String GENERATOR_NAME = "PastaGenerator";
    
    @Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name=GENERATOR_NAME, sequenceName=SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_pasta", nullable = false, unique = true)
    private Integer id;
    
    @NotNull
    @Column(name = "nm_pasta", nullable = false)
    @Size(max=250)
    private String nome;
    
    @Column(name="ds_pasta")
    @Size(max=250)
    private String descricao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo")
    private Processo processo;
    
    @NotNull
    @Column(name = "in_removivel", nullable = false)
    private Boolean removivel;
    
    @NotNull
    @Column(name = "in_sistema", nullable = false)
    private Boolean sistema;
    
    @NotNull
    @Column(name = "in_editavel", nullable = false)
    private Boolean editavel;
    
    @Column(name = "nr_ordem")
    private Integer ordem;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pasta")
    private List<Documento> documentosList = new ArrayList<>(0); 
    
    @PostPersist
    private void postPersist() {
        if (!processo.getPastaList().contains(this)) {
            processo.getPastaList().add(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getRemovivel() {
        return removivel;
    }

    public void setRemovivel(Boolean removivel) {
        this.removivel = removivel;
    }

    public List<Documento> getDocumentosList() {
        return documentosList;
    }

    public void setDocumentosList(List<Documento> documentosList) {
        this.documentosList = documentosList;
    }

    public Boolean getSistema() {
        return sistema;
    }

    public void setSistema(Boolean sistema) {
        this.sistema = sistema;
    }
    
    public String toString() {
    	return nome;
    }
    
    public Pasta makeCopy() throws CloneNotSupportedException {
    	Pasta cPasta = (Pasta) super.clone();
    	cPasta.setId(null);
    	cPasta.setProcesso(null);
    	List<Documento> cDocumentos = new ArrayList<>();
    	for (Documento documento : getDocumentosList()) {
    		Documento cDoc = documento.makeCopy();
    		cDoc.setPasta(cPasta);
    		cDocumentos.add(cDoc);
    	}
    	cPasta.setDocumentosList(cDocumentos);
    	return cPasta;
    }
    
    @Transient
    public String getTemplateNomePasta() {
    	return getNome() + " ({0})";
    }

    public Boolean getEditavel() {
        return editavel;
    }

    public void setEditavel(Boolean editavel) {
        this.editavel = editavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Pasta))
            return false;
        Pasta other = (Pasta) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }
}
