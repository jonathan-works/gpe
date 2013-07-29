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
package br.com.infox.jbpm;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.xml.sax.InputSource;

import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.JpdlXmlReader;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.itx.util.EntityUtil;


/**
 * Componente responsavel por verificar se os fluxos da aplicação 
 * já foram publicados no jBPM 
 *  
 * @author luizruiz
 *
 */

@Name(ProcessDeployVerifier.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies="org.jboss.seam.bpm.jbpm")
@Startup(depends="org.jboss.seam.bpm.jbpm")
public class ProcessDeployVerifier {

	public static final String NAME = "processDeployVerifier";
	private static final LogProvider LOG = Logging.getLogProvider(ProcessDeployVerifier.class);	
	
	@SuppressWarnings("unchecked")
	@Create
	public void init() {
		long time = new Date().getTime();
		UserTransaction transaction = Transaction.instance();
		try {
			if (!transaction.isActive()) {
				transaction.begin();
			} else {
				transaction = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		GraphSession graphSession = JbpmUtil.getGraphSession();
		List<ProcessDefinition> definitions = graphSession.findLatestProcessDefinitions();
		List<String> processNames = new ArrayList<String>();
		for (ProcessDefinition p : definitions) {
			processNames.add(p.getName());
		}
		List<Fluxo> list = EntityUtil.getEntityList(Fluxo.class);
		for (Fluxo f : list) {
			if (f.getAtivo() && f.getPublicado() && f.getXml() != null && !"".equals(f.getXml())) {
				if (!processNames.contains(f.getFluxo())) {
					ProcessDefinition instance = parseInstance(f.getXml());
					instance.setName(f.getFluxo());
					graphSession.deployProcessDefinition(instance);
					JbpmUtil.getJbpmSession().flush();
					Events.instance().raiseEvent(ProcessBuilder.POST_DEPLOY_EVENT, 
							instance);
					LOG.info(MessageFormat.format("Publicando fluxo {0}", f.getFluxo()));
				}
			}
		}
		if (transaction != null) {
			try {
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.info(MessageFormat.format("Tempo de publicacao: {0}", (new Date().getTime() - time)));
	}

	private ProcessDefinition parseInstance(String xml) {
	    StringReader stringReader = new StringReader(xml);
	    JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputSource(stringReader));
		return jpdlReader.readProcessDefinition();
	}
}