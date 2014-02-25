/* $Id: ControleFiltros.java 704 2010-08-12 23:21:10Z jplacerda $ */

package br.com.infox.epp.filter;

import static br.com.infox.epp.processo.situacao.filter.SituacaoProcessoFilter.FILTER_PAPEL_LOCALIZACAO;
import static br.com.infox.epp.processo.situacao.filter.SituacaoProcessoFilter.FILTER_PARAM_ID_LOCALIZACAO;
import static br.com.infox.epp.processo.situacao.filter.SituacaoProcessoFilter.FILTER_PARAM_ID_PAPEL;

import java.text.MessageFormat;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.processo.sigilo.filter.SigiloProcessoFilter;
import br.com.infox.epp.system.util.LogUtil;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.itx.util.ComponentUtil;

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
		if (!firstTime) {
			LOG.info("Ignorando execução duplicada. ");
			return;
		}
		firstTime = false;
		UsuarioLocalizacao usuarioLocalizacaoAtual = Authenticator.getUsuarioLocalizacaoAtual();
		
		//Iniciar os filtros
		HibernateUtil.setFilterParameter(FILTER_PAPEL_LOCALIZACAO, 
				FILTER_PARAM_ID_PAPEL, usuarioLocalizacaoAtual.getPapel().getIdPapel());
		HibernateUtil.setFilterParameter(FILTER_PAPEL_LOCALIZACAO, 
				FILTER_PARAM_ID_LOCALIZACAO, usuarioLocalizacaoAtual.getLocalizacao().getIdLocalizacao());
		
		HibernateUtil.setFilterParameter(SigiloProcessoFilter.FILTER_SIGILO_PROCESSO, 
				SigiloProcessoFilter.PARAM_ID_USUARIO, usuarioLocalizacaoAtual.getUsuario().getIdUsuarioLogin());
		
		LOG.info(MessageFormat.format(
				"Filtro executado para usuário [{0} | {1}]", usuarioLocalizacaoAtual.getUsuario(),
				LogUtil.getIdPagina()));
	}
	
	public static ControleFiltros instance() {
		return ComponentUtil.getComponent(NAME);
	}
}
