package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.entity.HistoricoParteProcesso;
import br.com.itx.component.AbstractHome;

@Name(HistoricoParteProcessoHome.NAME)
public class HistoricoParteProcessoHome extends AbstractHome<HistoricoParteProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "historicoProcessoHome";
	
}