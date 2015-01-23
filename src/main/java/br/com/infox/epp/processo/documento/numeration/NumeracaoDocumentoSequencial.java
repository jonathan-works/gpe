package br.com.infox.epp.processo.documento.numeration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.processo.entity.Processo;

@Entity
@Table(name="tb_numeracao_doc_sequencial")
@NamedQueries({
	@NamedQuery(name=NumeracaoDocumentoSequencial.GET_NEXT_VALUE, query=NumeracaoDocumentoSequencial.GET_NEXT_VALUE_QUERY)
})
public class NumeracaoDocumentoSequencial implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Id
    @SequenceGenerator(allocationSize=1, initialValue=1, name = "NumeracaoDocumentoSequencialGenerator", 
    	sequenceName = "sq_numeracao_doc_sequencial")
    @GeneratedValue(generator = "NumeracaoDocumentoSequencialGenerator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_numeracao_doc_sequencial", unique = true, nullable = false)
    private Integer id;
	
	@NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo", nullable = false, unique=true)
	private Processo processoRaiz;
	
	@Column(name="nr_proximo_numero", nullable = false)
	private Integer nextNumero;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Processo getProcessoRaiz() {
		return processoRaiz;
	}
	public void setProcessoRaiz(Processo processoRaiz) {
		this.processoRaiz = processoRaiz;
	}

	public Integer getNextNumero() {
		return nextNumero;
	}
	public void setNextNumero(Integer nextNumero) {
		this.nextNumero = nextNumero;
	}

	public static final String GET_NEXT_VALUE = "getNextValueForNumeracaoDocumentoSequencial";
	public static final String GET_NEXT_VALUE_QUERY = "select o from NumeracaoDocumentoSequencial o where o.processoRaiz = :processo";

}
