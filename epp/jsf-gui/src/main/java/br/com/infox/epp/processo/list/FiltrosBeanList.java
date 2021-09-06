package br.com.infox.epp.processo.list;

import java.io.Serializable;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.loglab.vo.PesquisaRequerenteVO;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.util.time.Periodo;
import lombok.Getter;
import lombok.Setter;

public class FiltrosBeanList implements Serializable {

	private static final long serialVersionUID = 1L;

	private Fluxo Fluxo;
	private String numeroProcesso;
	private Periodo dataInicio;
	private Periodo dataFim;
	private Natureza natureza;
	private Categoria categoria;
	private StatusProcesso statusProcesso;
	private UsuarioLogin usuarioLogin;
	@Setter
	@Getter
	private String cpf;
	@Setter
	@Getter
	private PesquisaRequerenteVO requerente;
	@Setter
	@Getter
	private String nomeRequerente;

	public Fluxo getFluxo() {
		return Fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		Fluxo = fluxo;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public Periodo getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Periodo dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Periodo getDataFim() {
		return dataFim;
	}

	public void setDataFim(Periodo dataFim) {
		this.dataFim = dataFim;
	}

	public Natureza getNatureza() {
		return natureza;
	}

	public void setNatureza(Natureza natureza) {
		this.natureza = natureza;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		this.statusProcesso = statusProcesso;
	}

	public UsuarioLogin getUsuarioLogin() {
		return usuarioLogin;
	}

	public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}

	public void clear() {
	    setDataInicio(new Periodo());
        setDataFim(new Periodo());
        setCategoria(null);
        setNatureza(null);
        setStatusProcesso(null);
        setNumeroProcesso(null);
        setCpf(null);
        setRequerente(null);
        setNomeRequerente(null);
	}

}
