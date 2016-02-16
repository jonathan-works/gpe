	package br.com.infox.epp.processo.comunicacao.prazo;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named(ContabilizadorPrazo.NAME)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ContabilizadorPrazo {
	
    public static final String NAME = "contabilizadorPrazo";
    public static final LogProvider LOG = Logging.getLogProvider(ContabilizadorPrazo.class);
    
    @Inject
    private PrazoComunicacaoService prazoComunicacaoService;
    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
			darCiencia(comunicacao, usuarioLogado, dataCiencia);
		} catch (DAOException e) {
			LOG.error("atribuirCiencia", e);
		}
    }

	protected void darCiencia(Processo comunicacao, UsuarioLogin usuarioLogado, Date dataCiencia) {
		prazoComunicacaoService.darCiencia(comunicacao, dataCiencia, usuarioLogado);
	}
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atribuirCumprimento() {
    	Processo comunicacao = JbpmUtil.getProcesso();
    	Date dataCumprimento = DateTime.now().toDate();
		MetadadoProcesso metadadoCumprimento = comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
		if (metadadoCumprimento != null) {
			dataCumprimento = metadadoCumprimento.getValue();
		}
    	try {
			darCumprimento(comunicacao, dataCumprimento);
		} catch (DAOException e) {
			LOG.error("atribuirCumprimento", e);
		}
    }

	protected void darCumprimento(Processo comunicacao, Date dataCumprimento) {
		prazoComunicacaoService.darCumprimento(comunicacao, dataCumprimento);
	}
    
}
