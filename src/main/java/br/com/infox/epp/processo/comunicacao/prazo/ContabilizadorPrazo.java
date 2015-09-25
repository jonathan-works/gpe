package br.com.infox.epp.processo.comunicacao.prazo;

import java.util.Date;

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
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.process.definition.annotations.DefinitionAvaliable;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

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
    private CalendarioEventosManager calendarioEventosManager = ComponentUtil.getComponent(CalendarioEventosManager.NAME);
    
    public void atribuirCiencia() {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	Date dataCiencia = DateTime.now().toDate();
    	if (usuarioLogado == null) {
    		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
    		usuarioLogado = usuarioLoginManager.find(idUsuarioSistema);
    		MetadadoProcesso metadadoCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA);
    		if (metadadoCiencia != null){
    			dataCiencia = metadadoCiencia.getValue();
    		}
    	} 
    	try {
			prazoComunicacaoService.darCiencia(comunicacao, dataCiencia, usuarioLogado);
		} catch (DAOException e) {
			LOG.error("atribuirCiencia", e);
		}
    }
    
    public void atribuirCumprimento() {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	Date dataCumprimento = DateTime.now().toDate();
    	if (usuarioLogado == null) {
    		Integer idUsuarioSistema = Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue());
    		usuarioLogado = usuarioLoginManager.find(idUsuarioSistema);
    		MetadadoProcesso metadadoCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
    		if (metadadoCumprimento != null) {
    			dataCumprimento = metadadoCumprimento.getValue();
    		}
    	}
    	try {
			prazoComunicacaoService.darCumprimento(comunicacao, dataCumprimento, usuarioLogado);
		} catch (DAOException e) {
			LOG.error("atribuirCumprimento", e);
		}
    }
    
}
