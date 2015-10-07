package br.com.infox.epp.documento.crud;

import java.util.Collection;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;

@Name(ClassificacaoDocumentoCrudAction.NAME)
public class ClassificacaoDocumentoCrudAction extends
        AbstractCrudAction<ClassificacaoDocumento, ClassificacaoDocumentoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "classificacaoDocumentoCrudAction";

    @In private TipoModeloDocumentoManager tipoModeloDocumentoManager;

    private Collection<TipoModeloDocumento> tiposModeloDocumento;

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
    }

    @Override
    public void newInstance() {
        setId(null);
        setInstance(new ClassificacaoDocumento());
        setTiposModeloDocumento(null);
    }

    @Override
    @Transactional
    public String save() {
        String save = super.save();
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
