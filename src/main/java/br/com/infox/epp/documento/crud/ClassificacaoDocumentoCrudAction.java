package br.com.infox.epp.documento.crud;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.entity.VinculoClassificacaoTipoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.manager.VinculoClassificacaoTipoDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named(ClassificacaoDocumentoCrudAction.NAME)
@ContextDependency
public class ClassificacaoDocumentoCrudAction extends
        AbstractCrudAction<ClassificacaoDocumento, ClassificacaoDocumentoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoCrudAction";

    @In private TipoModeloDocumentoManager tipoModeloDocumentoManager;
    @Inject private VinculoClassificacaoTipoDocumentoManager vinculoClassificacaoDocumentoManager;

    private TipoModeloDocumento tipoModeloDocumento;
    private Collection<TipoModeloDocumento> tiposModeloDocumento;

    private LogProvider LOG = Logging.getLogProvider(ClassificacaoDocumentoCrudAction.class);

    public TipoModeloDocumento getTipoModeloDocumento() {
        return tipoModeloDocumento;
    }

    public void setTipoModeloDocumento(TipoModeloDocumento tipoModeloDocumento) {
        this.tipoModeloDocumento = tipoModeloDocumento;
    }

    public Collection<TipoModeloDocumento> getTiposModeloDocumento() {
        if (tiposModeloDocumento == null) {
            populateTiposModeloDocumento();
        }
        return tiposModeloDocumento;
    }

    private void populateTiposModeloDocumento() {
        this.tiposModeloDocumento = tipoModeloDocumentoManager.getTiposModeloDocumentoAtivos();
    }

    public void setTiposModeloDocumento(Collection<TipoModeloDocumento> tiposModeloDocumento) {
        this.tiposModeloDocumento = tiposModeloDocumento;
    }

    public boolean getShowTiposModeloDocumento() {
        return getInstance() != null
                && (TipoDocumentoEnum.T.equals(getInstance().getInTipoDocumento()) || TipoDocumentoEnum.P
                        .equals(getInstance().getInTipoDocumento()));
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        if (id != null) {
            VinculoClassificacaoTipoDocumento vinculoClassificacaoTipoDocumento = vinculoClassificacaoDocumentoManager
                    .findVinculacaoByClassificacao(getInstance());
            setTipoModeloDocumento(vinculoClassificacaoTipoDocumento == null ? null : vinculoClassificacaoTipoDocumento
                    .getTipoModeloDocumento());
        }
    }

    @Override
    public void newInstance() {
        setId(null);
        setInstance(new ClassificacaoDocumento());
        setTipoModeloDocumento(null);
        setTiposModeloDocumento(null);
    }

    @Override
    @Transactional
    public String save() {
        String save = super.save();
        try {
            if (getTipoModeloDocumento() != null) {
                vinculoClassificacaoDocumentoManager.vincular(getInstance(), getTipoModeloDocumento());
            } else {
                vinculoClassificacaoDocumentoManager.desvincular(getInstance(), getTipoModeloDocumento());
            }
        } catch (DAOException e) {
            LOG.error("Erro ao atualizar vinculação entre tipo de modelo de documento e classificação de documento", e);
        }
        return save;
    }

    @Override
    protected String getManagerName() {
        return ClassificacaoDocumentoManager.NAME;
    }

    public boolean canShowSelectTipoDocumento() {
        TipoDocumentoEnum tipoDocumento = getInstance().getInTipoDocumento();
        return TipoDocumentoEnum.T.equals(tipoDocumento) || TipoDocumentoEnum.P.equals(tipoDocumento);
    }
}
