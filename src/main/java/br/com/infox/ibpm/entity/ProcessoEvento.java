package br.com.infox.ibpm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.query.ProcessoEventoQuery;
 
/**
 * 
 * @author Infox
 * 
 */
@Entity
@Table(name = ProcessoEvento.TABLE_NAME, schema="public")
@NamedQueries(value={@NamedQuery(name=ProcessoEventoQuery.LIST_EVENTO_NAO_PROCESSADO, 
								 query=ProcessoEventoQuery.LIST_EVENTO_NAO_PROCESSADO_QUERY)})
public class ProcessoEvento implements java.io.Serializable {

	public static final String TABLE_NAME = "tb_processo_evento";
	private static final long serialVersionUID = 1L;

	private int idProcessoEvento;
	private Processo processo;
	private ProcessoDocumento processoDocumento;
	private Evento evento;
	private UsuarioLogin usuario;
	private Date dataAtualizacao;
	private Long idJbpmTask;
	private Long idProcessInstance;
	private Tarefa tarefa;
	private String nomeUsuario;
	private String cpfUsuario;
	private String cnpjUsuario;
	private boolean processado = false;
	private boolean verificadoProcessado = false;
	
	@SequenceGenerator(name = "generator", sequenceName = "public.sq_tb_processo_evento")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_processo_evento", unique = true, nullable = false)
	public int getIdProcessoEvento() {
		return idProcessoEvento;
	}
	
	public void setIdProcessoEvento(int idProcessoEvento) {
		this.idProcessoEvento = idProcessoEvento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo", nullable = false)
	@NotNull
	public Processo getProcesso() {
		return processo;
	}
	
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_processo_documento")
	public ProcessoDocumento getProcessoDocumento() {
		return processoDocumento;
	}
	
	public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
		this.processoDocumento = processoDocumento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_evento", nullable = false)
	@NotNull
	public Evento getEvento() {
		return evento;
	}
	
	public void setEvento(Evento evento) {
		this.evento = evento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	public UsuarioLogin getUsuario() {
		return usuario;
	}
	
	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_atualizacao")
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	
	@Column(name = "id_jbpm_task")
	public Long getIdJbpmTask() {
		return idJbpmTask;
	}

	public void setIdJbpmTask(Long idJbpmTask) {
		this.idJbpmTask = idJbpmTask;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tarefa")
	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	@Column(name="id_process_instance")
	public Long getIdProcessInstance() {
		return idProcessInstance;
	}	

	public void setIdProcessInstance(Long idProcessInstance) {
		this.idProcessInstance = idProcessInstance;
	}

	@Column(name = "ds_nome_usuario", length = 100)
	@Size(max = 100)
	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	@Column(name = "ds_cpf_usuario", length = 50)
	@Size(max = 50)
	public String getCpfUsuario() {
		return cpfUsuario;
	}

	public void setCpfUsuario(String cpfUsuario) {
		this.cpfUsuario = cpfUsuario;
	}

	@Column(name = "ds_cnpj_usuario", length = 50)
	@Size(max = 50)
	public String getCnpjUsuario() {
		return cnpjUsuario;
	}

	public void setCnpjUsuario(String cnpjUsuario) {
		this.cnpjUsuario = cnpjUsuario;
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProcessoEvento)) {
			return false;
		}
		ProcessoEvento other = (ProcessoEvento) obj;
		if (getIdProcessoEvento() != other.getIdProcessoEvento()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdProcessoEvento();
		return result;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	@Column(name = "in_processado", nullable=false)
	public boolean isProcessado() {
		return processado;
	}

	public void setVerificadoProcessado(boolean verificadoProcessado) {
		this.verificadoProcessado = verificadoProcessado;
	}

	@Column(name = "in_verificado_processado", nullable=false)
	public boolean isVerificadoProcessado() {
		return verificadoProcessado;
	}	
	
}