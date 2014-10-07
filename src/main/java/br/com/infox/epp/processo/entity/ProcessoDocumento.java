package br.com.infox.epp.processo.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.infox.epp.processo.documento.entity.Documento;

@Entity
@Table(name = ProcessoDocumento.TABLE_NAME)
@PrimaryKeyJoinColumn
@DiscriminatorValue(value = "PD")
public class ProcessoDocumento extends Processo {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_processo_documento";
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", nullable = true)
	private Documento documento;

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
	
}
