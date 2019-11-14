package br.com.infox.epp.system.util;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(CarregarParametrosAplicacao.NAME)
@Scope(ScopeType.APPLICATION)
@Install()
@Startup()
@BypassInterceptors
public class CarregarParametrosAplicacao {

    public static final String NAME = "carregarParametrosAplicacao";
    private static final LogProvider LOG = Logging.getLogProvider(CarregarParametrosAplicacao.class);

    @Create
    public void init() {
        StopWatch sw = new StopWatch();
        sw.start();
        for (Parametro parametro : getParametroAtivoList()) {
            Contexts.getApplicationContext().set(parametro.getNomeVariavel().trim(), parametro.getValorVariavel());
        }
        LOG.info(".init(): " + sw.getTime());
    }

    private List<Parametro> getParametroAtivoList() {
        ParametroManager parametroManager = ComponentUtil.getComponent(ParametroManager.NAME);
        return parametroManager.listParametrosAtivos();
    }

}
