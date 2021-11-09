package br.com.infox.epp.tarefaexterna;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.epp.classesautomaticas.EppStartupListenerExtended;

@Singleton
public class DocumentoUploadTarefaExternaCleaner implements EppStartupListenerExtended{

	@Inject
	private DocumentoUploadTarefaExternaService documentoUploadTarefaExternaService;

    @Override
    public void processInit() throws Exception {
        Lifecycle.beginCall();
        documentoUploadTarefaExternaService.removerTodosCascade();
        Lifecycle.endCall();
    }

}