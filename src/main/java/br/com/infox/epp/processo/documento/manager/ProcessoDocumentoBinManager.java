package br.com.infox.epp.processo.documento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;

@Name(ProcessoDocumentoBinManager.NAME)
@AutoCreate
public class ProcessoDocumentoBinManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoBinManager";
}
