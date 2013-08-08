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

import org.jboss.seam.Component;

import br.com.infox.ibpm.entity.ItemTipoDocumento;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.itx.component.AbstractHome;


public abstract class AbstractLocalizacaoHome<T> extends
		AbstractHome<Localizacao> {

	private static final long serialVersionUID = 1L;

	public void setLocalizacaoIdLocalizacao(Integer id) {
		setId(id);
	}

	public Integer getLocalizacaoIdLocalizacao() {
		return (Integer) getId();
	}

	@Override
	protected Localizacao createInstance() {
		Localizacao localizacao = new Localizacao();
		EnderecoHome enderecoHome = (EnderecoHome) Component.getInstance(
				"enderecoHome", false);
		if (enderecoHome != null) {
			localizacao.setEndereco(enderecoHome.getDefinedInstance());
		}
		LocalizacaoHome localizacaoHome = (LocalizacaoHome) Component.getInstance("localizacaoHome", false);
		if (localizacaoHome != null){
			localizacao.setLocalizacaoPai(localizacaoHome.getDefinedInstance());
		}
		return localizacao;
	}

	@Override
	public String remove() {
		EnderecoHome endereco = (EnderecoHome) Component.getInstance(
				"enderecoHome", false);
		if (endereco != null) {
			endereco.getInstance().getLocalizacaoList().remove(instance);
		}
		LocalizacaoHome localizacao = (LocalizacaoHome) Component.getInstance("localizacaoHome", false);
		if (localizacao != null){
			localizacao.getInstance().getLocalizacaoList().remove(instance);
		}
		return super.remove();
	}

	@Override
	public String remove(Localizacao obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	@Override
	public String persist() {		
		String action = super.persist();
		if (action != null && getInstance().getLocalizacaoPai() != null){
			List<Localizacao> localizacaoPaiList = getInstance().getLocalizacaoPai().getLocalizacaoList();
			if (!localizacaoPaiList.contains(instance)){
				getEntityManager().refresh(getInstance().getLocalizacaoPai());
			}
		}
		return action;
	}
	
	public List<ItemTipoDocumento> getItemTipoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getItemTipoDocumentoList();
	}
	
	public List<Localizacao> getLocalizacaoList(){
		return getInstance() == null ? null : getInstance().getLocalizacaoList();
	}
	
	public List<UsuarioLocalizacao> getUsuarioLocalizacaoList() {
		return getInstance() == null ? null : getInstance()
				.getUsuarioLocalizacaoList();
	}
}