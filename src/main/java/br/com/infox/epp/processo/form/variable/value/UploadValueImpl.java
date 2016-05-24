package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.seam.path.PathResolver;

public class UploadValueImpl extends FileTypedValue {

    public UploadValueImpl(Documento documento) {
        super(documento, ValueType.UPLOAD);
    }
    
    public String getUrlDownload() {
        PathResolver pathResolver = BeanManager.INSTANCE.getReference(PathResolver.class);
        return String.format("%s/downloadDocumento.seam?id=%d", pathResolver.getContextPath(), documento.getId());
    }
    
}
