/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.List;

import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.infox.ibpm.entity.Endereco;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.itx.component.AbstractHome;


public abstract class AbstractUsuarioHome<T> extends AbstractHome<UsuarioLogin> {

	private static final long serialVersionUID = 1L;

	public void setUsuarioIdUsuario(Integer id) {
		setId(id);
	}

	public Integer getUsuarioIdUsuario() {
		return (Integer) getId();
	}

	@Override
	protected UsuarioLogin createInstance() {
		return new UsuarioLogin();
	}

	@Override
	public String remove(UsuarioLogin obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	public List<Fluxo> getFluxoList() {
		return getInstance() == null ? null : getInstance().getFluxoList();
	}

	public List<Endereco> getEnderecoList() {
		return getInstance() == null ? null : getInstance().getEnderecoList();
	}

	public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
		return getInstance() == null ? null : getInstance()
				.getUsuarioLocalizacaoList();
	}

	public List<BloqueioUsuario> getBloqueioUsuarioList() {
		return getInstance() == null ? null : getInstance()
				.getBloqueioUsuarioList();
	}

	public List<ProcessoDocumentoBin> getProcessoDocumentoBinList() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoBinList();
	}

	public List<Processo> getProcessoListForIdUsuarioCadastroProcesso() {
		return getInstance() == null ? null : getInstance()
				.getProcessoListForIdUsuarioCadastroProcesso();
	}
	

	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioInclusao() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoListForIdUsuarioInclusao();
	}


	public List<ProcessoDocumento> getProcessoDocumentoListForIdUsuarioExclusao() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoListForIdUsuarioExclusao();
	}

}