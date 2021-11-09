package br.com.infox.epp.tarefaexterna;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ParametrosTarefaExternaService {

    @Inject
    private ParametroManager parametroManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(String sinalTarefaExterna, String modeloDocumentoChaveConsulta, String modeloDocumentoDownloadPDF) {
        update(Parametros.SINAL_TAREFA_EXTERNA.getLabel(), sinalTarefaExterna);
        Contexts.getApplicationContext().set(Parametros.SINAL_TAREFA_EXTERNA.getLabel(), sinalTarefaExterna);

        update(Parametros.MODELO_DOC_CHAVE_CONSULTA.getLabel(), modeloDocumentoChaveConsulta);
        Contexts.getApplicationContext().set(Parametros.MODELO_DOC_CHAVE_CONSULTA.getLabel(), modeloDocumentoChaveConsulta);

        update(Parametros.MODELO_DOC_DOWNLOAD_PDF.getLabel(), modeloDocumentoDownloadPDF);
        Contexts.getApplicationContext().set(Parametros.MODELO_DOC_DOWNLOAD_PDF.getLabel(), modeloDocumentoDownloadPDF);
    }

    private void update(String codParametro, String valor) {
        Parametro parametro = parametroManager.getParametro(codParametro);
        if(parametro == null) {
            throw new EppConfigurationException(String.format("Parâmetro não encontrado: %s", codParametro));
        }
        parametro.setValorVariavel(valor);
        parametro.setUsuarioModificacao(Authenticator.getUsuarioLogado());
        parametro.setDataAtualizacao(Calendar.getInstance().getTime());
        parametroManager.persist(parametro);
    }

}
