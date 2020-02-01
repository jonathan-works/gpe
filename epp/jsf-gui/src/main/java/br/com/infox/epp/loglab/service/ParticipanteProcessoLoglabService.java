package br.com.infox.epp.loglab.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ParticipanteProcessoLoglabService extends PersistenceController {

    @Inject
    private ProcessoManager processoManager;
    @Inject
    private ServidorContribuinteService servidorContribuinteService;
    @Inject
    private EmpresaService empresaService;

    public void persistenciaIniciarProcessoView(Processo processo,
            List<MetadadoProcesso> metadados, List<ParticipanteProcesso> participantes,
            List<ServidorContribuinteVO> listServidorContribuinteVO, List<EmpresaVO> listEmpresaVO) {
        processoManager.gravarProcessoMetadadoParticipantePasta(processo, metadados, participantes);
        for (ServidorContribuinteVO servidorContribuinteVO : listServidorContribuinteVO) {
            servidorContribuinteService.gravar(servidorContribuinteVO);
        }

        for (EmpresaVO empresaVO : listEmpresaVO) {
            empresaService.gravar(empresaVO);
        }
    }

    public void gravarProcessoMetadadoParticipantePastaEmpresa() {

    }

}
