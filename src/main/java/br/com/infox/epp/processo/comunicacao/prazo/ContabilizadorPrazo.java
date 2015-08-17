package br.com.infox.epp.processo.comunicacao.prazo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.process.definition.annotations.DefinitionAvaliable;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Name(ContabilizadorPrazo.NAME)
@Scope(ScopeType.STATELESS)
@DefinitionAvaliable
@Transactional
public class ContabilizadorPrazo {
	
    public static final String NAME = "contabilizadorPrazo";
    public static final LogProvider LOG = Logging.getLogProvider(ContabilizadorPrazo.class);
    
    @In
    private PrazoComunicacaoService prazoComunicacaoService;
    @In
    private UsuarioLoginManager usuarioLoginManager;
    
    public void atribuirCiencia() {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	if (usuarioLogado == null) {
    		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
    		usuarioLogado = usuarioLoginManager.find(idUsuarioSistema);
    	}
    	try {
			prazoComunicacaoService.darCiencia(comunicacao, DateTime.now().toDate(), usuarioLogado);
		} catch (DAOException e) {
			LOG.error("atribuirCiencia", e);
		}
    }
    
    public void atribuirCumprimento() {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	if (usuarioLogado == null) {
    		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
    		usuarioLogado = usuarioLoginManager.find(idUsuarioSistema);
    	}
    	try {
			prazoComunicacaoService.darCumprimento(comunicacao, DateTime.now().toDate(), usuarioLogado);
		} catch (DAOException e) {
			LOG.error("atribuirCumprimento", e);
		}
    }
    
}
