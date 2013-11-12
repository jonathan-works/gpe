/* $Id: ControleFiltros.java 704 2010-08-12 23:21:10Z jplacerda $ */

package br.com.infox.ibpm.component;

import java.text.MessageFormat;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.processo.situacao.filter.SituacaoProcessoFilter;
import br.com.infox.epp.system.util.LogUtil;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.component.MeasureTime;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.HibernateUtil;

@Name(ControleFiltros.NAME)
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class ControleFiltros {
	
	public static final String INICIALIZAR_FILTROS = "br.com.infox.cliente.component.inicializarFiltros";
	public static final String NAME = "controleFiltros";
	private static final LogProvider LOG = Logging.getLogProvider(ControleFiltros.class);
	private boolean firstTime = true;
	
	@Observer({INICIALIZAR_FILTROS, TarefasTreeHandler.FILTER_TAREFAS_TREE})
	public void iniciarFiltro() {	
		MeasureTime mt = new MeasureTime(true);
		if (!firstTime) {
			LOG.info("Ignorando execução duplicada. " + mt.getTime());
			return;
		}
		firstTime = false;
		UsuarioLocalizacao usuarioLocalizacaoAtual = Authenticator.getUsuarioLocalizacaoAtual();
		
		//Iniciar os filtros
		HibernateUtil.setFilterParameter(SituacaoProcessoFilter.FILTER_PAPEL_LOCALIZACAO, 
				SituacaoProcessoFilter.FILTER_PARAM_ID_PAPEL, usuarioLocalizacaoAtual.getPapel().getIdPapel());
		HibernateUtil.setFilterParameter(SituacaoProcessoFilter.FILTER_PAPEL_LOCALIZACAO, 
				SituacaoProcessoFilter.FILTER_PARAM_ID_LOCALIZACAO, usuarioLocalizacaoAtual.getLocalizacao().getIdLocalizacao());
		
		LOG.info(MessageFormat.format(
				"Filtro executado para usuário [{0} | {1}] ({2} ms)", usuarioLocalizacaoAtual.getUsuario(),
				LogUtil.getIdPagina(), mt.getTime()));
	}
	
	public static ControleFiltros instance() {
		return ComponentUtil.getComponent(NAME);
	}
}
