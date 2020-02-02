package br.com.infox.epp.loglab.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.ParticipanteProcessoVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.pessoa.manager.PessoaService;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.epp.tipoParte.TipoParteSearch;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ParticipanteProcessoLoglabService extends PersistenceController {

    @Inject
    private ProcessoManager processoManager;
    @Inject
    private ServidorContribuinteService servidorContribuinteService;
    @Inject
    private EmpresaService empresaService;
    @Inject
    private ParticipanteProcessoManager participanteProcessoManager;
    @Inject
    private TipoParteSearch tipoParteSearch;
    @Inject
    private PessoaService pessoaService;
    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private PessoaJuridicaManager pessoaJuridicaManager;
    @Inject
    private MeioContatoManager meioContatoManager;


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

    public ParticipanteProcesso gravarParticipanteProcesso(ParticipanteProcessoVO participanteProcessoVO) {
        Processo processo = processoManager.find(participanteProcessoVO.getIdProcesso());
        TipoParte tipoParte = tipoParteSearch.getTipoParteByIdentificador(participanteProcessoVO.getCdTipoParte());

        validaExistenciaParticipante(participanteProcessoVO, processo, tipoParte);

        ParticipanteProcesso participanteProcesso = participanteProcessoFromParticipanteProcessoVO(participanteProcessoVO);
        participanteProcesso.setTipoParte(tipoParte);
        participanteProcesso.setProcesso(processo);
        if (participanteProcessoVO.getTipoPessoa() == TipoPessoaEnum.F) {
            PessoaFisica pessoaFisica = servidorContribuinteService.convertPessoaFisicaFromServidorContribuinteVO(participanteProcessoVO.getServidorContribuinteVO());
            if(pessoaFisica.getIdPessoa() == null) {
                pessoaFisicaManager.persist(pessoaFisica);
            } else {
                pessoaFisicaManager.update(pessoaFisica);
            }
            includeMeioContato(pessoaFisica, participanteProcessoVO.getServidorContribuinteVO().getEmail());
            participanteProcesso.setPessoa(pessoaFisica);
        } else {
            PessoaJuridica pessoaJuridica = empresaService.convertPessoaJuridicaFromServidorContribuinteVO(participanteProcessoVO.getEmpresaVO());
            if(pessoaJuridica.getIdPessoa() == null) {
                pessoaJuridicaManager.persist(pessoaJuridica);
            } else {
                pessoaJuridicaManager.update(pessoaJuridica);
            }
            participanteProcesso.setPessoa(pessoaJuridica);
        }

        participanteProcesso = participanteProcessoManager.persist(participanteProcesso);
        processoManager.update(processo);

        if (participanteProcessoVO.getTipoPessoa() == TipoPessoaEnum.F) {
            servidorContribuinteService.gravar(participanteProcessoVO.getServidorContribuinteVO());
        } else {
            empresaService.gravar(participanteProcessoVO.getEmpresaVO());
        }

        return participanteProcesso;

    }

    private void validaExistenciaParticipante(ParticipanteProcessoVO participanteProcessoVO, Processo processo, TipoParte tipo){
        ParticipanteProcesso pai = participanteProcessoManager.find(participanteProcessoVO.getIdParticipantePai());
        if (participanteProcessoVO.getIdPessoa() == null) {
            return;
        }
        if (pai != null && pai.getPessoa().getIdPessoa().equals(participanteProcessoVO.getIdPessoa())) {
            throw new BusinessException("Participante não pode ser filho dele mesmo");
        }
        Pessoa pessoa = pessoaService.getByCodigo(participanteProcessoVO.getCodigoPessoa(), participanteProcessoVO.getTipoPessoa());
        boolean existe = participanteProcessoManager.existeParticipanteByPessoaProcessoPaiTipoLock(pessoa, processo, pai, tipo);
        if (existe) {
            throw new BusinessException("Participante já cadastrado");
        }
    }

    private void includeMeioContato(Pessoa pessoa, String email) throws DAOException {
        if (email == null) {
            return;
        }
        boolean jaPossuiEmail = meioContatoManager.existeMeioContatoByPessoaTipoValor(pessoa, TipoMeioContatoEnum.EM, email);
        if (!jaPossuiEmail) {
            MeioContato meioContato = new MeioContato();
            meioContato.setPessoa(pessoa);
            meioContato.setMeioContato(email);
            meioContato.setTipoMeioContato(TipoMeioContatoEnum.EM);
            meioContatoManager.persist(meioContato);
        }
    }

    private ParticipanteProcesso participanteProcessoFromParticipanteProcessoVO(ParticipanteProcessoVO vo) {
        ParticipanteProcesso participanteProcesso = new ParticipanteProcesso();
        participanteProcesso.setAtivo(Boolean.TRUE);
        participanteProcesso.setDataInicio(vo.getDataInicio());
        participanteProcesso.setDataFim(vo.getDataFim());
        participanteProcesso.setNome(vo.getNome());
        participanteProcesso.setParticipantePai(participanteProcessoManager.find(vo.getIdParticipantePai()));
        return participanteProcesso;
    }
}
