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

    @Create
    public void init() {
        createClassificacaoDocumentoAcessoDireto();
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
}
