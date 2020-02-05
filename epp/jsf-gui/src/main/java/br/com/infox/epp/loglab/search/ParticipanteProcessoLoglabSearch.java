package br.com.infox.epp.loglab.search;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.loglab.dto.ParticipanteProcessoLogLabDTO;
import br.com.infox.epp.loglab.vo.EmpresaVO;
import br.com.infox.epp.loglab.vo.ServidorContribuinteVO;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;
import br.com.infox.epp.processo.partes.entity.TipoParte_;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ParticipanteProcessoLoglabSearch extends PersistenceController {

    @Inject
    private EmpresaSearch empresaSearch;
    @Inject
    private ServidorContribuinteSearch servidorContribuinteSearch;

    public List<ParticipanteProcessoLogLabDTO> getListaParticipanteProcessoLoglabDTOByCodTipoParteAndIdProcesso(
            Integer idProcesso, String codTipoParte) {
        List<ParticipanteProcesso> listaParticipante = getListaParticipanteProcessoByCodTipoParteAndIdProcesso(
                idProcesso, codTipoParte);

        List<ParticipanteProcessoLogLabDTO> listaDTO = new ArrayList<>();
        for (ParticipanteProcesso participante : listaParticipante) {
            if (participante.getPessoa().getCodigo().length() == 14) {
                listaDTO.add(participanteDTOEmpresa(participante.getPessoa().getCodigo()));
            } else if (participante.getPessoa().getCodigo().length() == 11) {
                listaDTO.add(participanteDTOServidorContribuinte(participante.getPessoa().getCodigo()));
            }
        }

        return listaDTO;
    }

    private ParticipanteProcessoLogLabDTO participanteDTOServidorContribuinte(String cpf) {
        ParticipanteProcessoLogLabDTO participanteDTO = new ParticipanteProcessoLogLabDTO();
        participanteDTO.setTipoPessoa(TipoPessoaEnum.F);
        ServidorContribuinteVO servidorVO = servidorContribuinteSearch.getServidorByCPF(cpf);
        if (servidorVO != null) {
            participanteDTO.setNome(servidorVO.getNomeCompleto());
            participanteDTO.setCpf(servidorVO.getCpf());
            participanteDTO.setMatricula(servidorVO.getMatricula());
            participanteDTO.setCargo(!StringUtil.isEmpty(servidorVO.getCargoCarreira()) ? servidorVO.getCargoCarreira()
                    : servidorVO.getCargoComissao());
            participanteDTO
                    .setFuncao(!StringUtil.isEmpty(servidorVO.getOcupacaoCarreira()) ? servidorVO.getOcupacaoCarreira()
                            : servidorVO.getOcupacaoComissao());
            participanteDTO.setOrgaoLotado(servidorVO.getOrgao());
        } else {
            ServidorContribuinteVO contribuinte = servidorContribuinteSearch.getContribuinteByCPF(cpf);
            if (contribuinte != null) {
                participanteDTO.setNome(contribuinte.getNomeCompleto());
                participanteDTO.setCpf(contribuinte.getCpf());
            } else {
                throw new BusinessException("Pessoa não encontrada com o CPF: " + cpf);
            }
        }
        return participanteDTO;
    }

    private ParticipanteProcessoLogLabDTO participanteDTOEmpresa(String cnpj) {
        EmpresaVO empresaVO = empresaSearch.getEmpresaVOByCnpj(cnpj);
        if (empresaVO == null) {
            throw new BusinessException("Empresa não encontrada com o CNPJ: " + cnpj);
        }
        ParticipanteProcessoLogLabDTO participanteDTO = new ParticipanteProcessoLogLabDTO();
        participanteDTO.setTipoPessoa(TipoPessoaEnum.J);
        participanteDTO.setNomeFantasia(empresaVO.getNomeFantasia());
        participanteDTO.setCnpj(empresaVO.getCnpj());
        participanteDTO.setRazaoSocial(empresaVO.getRazaoSocial());
        return participanteDTO;
    }

    private List<ParticipanteProcesso> getListaParticipanteProcessoByCodTipoParteAndIdProcesso(Integer idProcesso,
            String codTipoParte) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ParticipanteProcesso> query = cb.createQuery(ParticipanteProcesso.class);
        Root<ParticipanteProcesso> participante = query.from(ParticipanteProcesso.class);
        query.select(participante);
        query.where(cb.and(cb.isTrue(participante.get(ParticipanteProcesso_.ativo)),
                cb.equal(participante.get(ParticipanteProcesso_.processo).get(Processo_.idProcesso), idProcesso),
                cb.equal(participante.get(ParticipanteProcesso_.tipoParte).get(TipoParte_.identificador),
                        codTipoParte)));

        return getEntityManager().createQuery(query).getResultList();
    }

}
