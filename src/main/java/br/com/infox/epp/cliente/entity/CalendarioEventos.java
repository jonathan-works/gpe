package br.com.infox.epp.cliente.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.util.constants.LengthConstants;

@Entity
@Table(schema="public", name=CalendarioEventos.TABLE_NAME)
public class CalendarioEventos implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_calendario_eventos";
	
	private int idCalendarioEvento;
	private Localizacao localizacao;
	private String descricaoEvento;
	private Integer dia;
	private Integer mes;
	private Integer ano;
	private Date dataEvento;
	private Boolean repeteAno = Boolean.TRUE;
	
	@SequenceGenerator(name="generator", sequenceName="public.sq_tb_calendario_eventos")
	@Id
	@GeneratedValue(generator="generator")
	@Column(name="id_calendario_evento", unique=true, nullable=false)
	public int getIdCalendarioEvento() {
		return idCalendarioEvento;
	}
	public void setIdCalendarioEvento(int idCalendarioEvento) {
		this.idCalendarioEvento = idCalendarioEvento;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_localizacao", nullable=false)
	@NotNull
	public Localizacao getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@Column(name="ds_evento", length=LengthConstants.DESCRICAO_PADRAO, nullable=false)
	@Size(max=LengthConstants.DESCRICAO_PADRAO)
	@NotNull
	public String getDescricaoEvento() {
		return descricaoEvento;
	}
	public void setDescricaoEvento(String descricaoEvento) {
		this.descricaoEvento = descricaoEvento;
	}
	
	@Column(name="dt_dia", nullable=false)
	@NotNull
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	
	@Column(name="dt_mes", nullable=false)
	@NotNull
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	
	@Column(name="dt_ano")
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	@Transient
	public Date getDataEvento() {
		return dataEvento;
	}
	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	@Transient
	public Boolean getRepeteAno() {
		return repeteAno;
	}
	public void setRepeteAno(Boolean repeteAno) {
		this.repeteAno = repeteAno;
	}

	@Transient
	public String getStringDataEvento()	{
		String result;
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.DATE, dia);
		calendar.set(Calendar.MONTH, mes-1);
		
		if (ano == null)	{
			calendar.set(Calendar.YEAR, 0);
		} else {
			calendar.set(Calendar.YEAR, ano);
		}
		
		if (ano == null)	{
			result = new SimpleDateFormat("dd/MM/----").format(calendar.getTime());
		} else {
			result = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
		}
		
		return result;
	}
	
}
