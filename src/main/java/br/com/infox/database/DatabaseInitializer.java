package br.com.infox.database;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;

@Scope(ScopeType.APPLICATION)
@Install
@Startup
@Name(DatabaseInitializer.NAME)
public class DatabaseInitializer {

    public static final String NAME = "databaseInitializer";
    private static final LogProvider LOG = Logging.getLogProvider(DatabaseInitializer.class);

    @In
    private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    @In
    private RecursoManager recursoManager;

    @Create
    public void init() {
        createClassificacaoDocumentoAcessoDireto();
        createPrioridadeProcessoChangerRecurso();
    }

    private void createClassificacaoDocumentoAcessoDireto() {
        if (!tipoProcessoDocumentoManager.existsClassificaoAcessoDireto()) {
            TipoProcessoDocumento acessoDireto = new TipoProcessoDocumento();
            acessoDireto.setCodigoDocumento(tipoProcessoDocumentoManager.CODIGO_CLASSIFICACAO_ACESSO_DIRETO);
            acessoDireto.setTipoProcessoDocumento("Acesso Direto");
            acessoDireto.setInTipoDocumento(TipoDocumentoEnum.D);
            acessoDireto.setTipoNumeracao(TipoNumeracaoEnum.S);
            acessoDireto.setSistema(false);
            acessoDireto.setPublico(true);
            acessoDireto.setAtivo(true);
            try {
                tipoProcessoDocumentoManager.persist(acessoDireto);
            } catch (DAOException e) {
                LOG.error("Não foi possível criar a classificação documento com acesso direto", e);
            }
        }
    }
    
    private void createPrioridadeProcessoChangerRecurso() {
        String identificador = "/pages/Processo/Consulta/setPrioridadeProcesso";
        if (!recursoManager.existsRecurso(identificador)) {
            Recurso recurso = new Recurso();
            recurso.setIdentificador(identificador);
            recurso.setNome(identificador);
            try {
                recursoManager.persist(recurso);
            } catch (DAOException e) {
                LOG.error("Não foi possível criar o recurso de mudança de prioridade da tela de consulta processo", e);
            }
        }
    }
}
