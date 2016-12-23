package br.com.infox.epp.processo.metadado.auditoria;

import javax.persistence.EntityManager;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;

public class MetadadoProcessoListener extends PersistenceController {

	@PostPersist
	public void postPersist(MetadadoProcesso metadadoProcesso) {
		gravarLog(TipoOperacaoLogEnum.I, metadadoProcesso);
	}

	@PostUpdate
	public void postUpdate(MetadadoProcesso metadadoProcesso) {
		gravarLog(TipoOperacaoLogEnum.U, metadadoProcesso);
	}

	@PostRemove
	public void postRemove(MetadadoProcesso metadadoProcesso) {
		gravarLog(TipoOperacaoLogEnum.D, metadadoProcesso);
	}
	
	private void gravarLog(TipoOperacaoLogEnum acao, MetadadoProcesso metadadoProcesso) {
		HistoricoMetadadoProcesso log = new HistoricoMetadadoProcesso();
		log.setIdMetadadoProcesso(metadadoProcesso.getId());
		log.setNome(metadadoProcesso.getMetadadoType());
		log.setValor(metadadoProcesso.getValor());
		log.setTipo(metadadoProcesso.getMetadadoType());
		log.setIdProcesso(metadadoProcesso.getProcesso().getIdProcesso().longValue());
		log.setDataRegistro(DateTime.now().toDate());
		log.setAcao(acao.getLabel());
		
		UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
		if(usuarioLogado != null)
			log.setIdUsuarioLogado(usuarioLogado.getIdUsuarioLogin().longValue());
		
		 EntityManager em = EntityManagerProducer.instance().getEntityManagerNotManaged();
		 em.persist(log);
		 em.flush();
		 em.close();
	}

}
