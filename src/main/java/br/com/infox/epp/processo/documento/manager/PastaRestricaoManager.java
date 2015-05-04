package br.com.infox.epp.processo.documento.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.dao.PastaRestricaoDAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;

@AutoCreate
@Name(PastaRestricaoManager.NAME)
public class PastaRestricaoManager extends Manager<PastaRestricaoDAO, PastaRestricao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaRestricaoManager";
    
    @In
    ParticipanteProcessoManager participanteProcessoManager;
    @In
    PastaManager pastaManager;

    public Map<Integer, PastaRestricaoBean> loadRestricoes(Processo processo, UsuarioLogin usuario, Localizacao localizacao, Papel papel) throws DAOException {
        Map<Integer, PastaRestricaoBean> restricoes = new HashMap<>();
        List<Pasta> pastas = pastaManager.getByProcesso(processo);
        
        for (Pasta pasta : pastas) {
            PastaRestricaoBean restricaoBean = new PastaRestricaoBean();
            List<PastaRestricao> restricoesDaPasta = getByPasta(pasta);
            PastaRestricao restricaoDefault = null;
            Boolean olharRestricaoDefault = true;
            
            for (PastaRestricao restricao : restricoesDaPasta) {
                PastaRestricaoEnum tipoRestricao = restricao.getTipoPastaRestricao();
                
                if (PastaRestricaoEnum.P.equals(tipoRestricao)) {
                    populateBeanPapel(restricaoBean, restricao, papel);
                    olharRestricaoDefault = false;
                } else if (PastaRestricaoEnum.R.equals(tipoRestricao)) {
                    populateBeanParticipante(restricaoBean, restricao);
                    olharRestricaoDefault = false;
                } else if (PastaRestricaoEnum.L.equals(tipoRestricao)) {
                    populateBeanLocalizacao(restricaoBean, restricao, localizacao);
                    olharRestricaoDefault = false;
                } else if (PastaRestricaoEnum.D.equals(tipoRestricao)) {
                    restricaoDefault = restricao;
                }
            }
            if (olharRestricaoDefault) {
                // TODO tratar o caso da restricaoDefault
            }
            
            restricoes.put(pasta.getId(), restricaoBean);
        }
        
        return restricoes;
    }
    
    private void populateBeanPapel(PastaRestricaoBean restricaoBean, PastaRestricao restricao, Papel papel) {
        // TODO implementar
    }

    private void populateBeanParticipante(PastaRestricaoBean restricaoBean, PastaRestricao restricao) {
        // TODO implementar
    }
    
    private void populateBeanLocalizacao(PastaRestricaoBean restricaoBean, PastaRestricao restricao, Localizacao localizacao) {
        // TODO implementar
    }
    
    private List<PastaRestricao> getByPasta(Pasta pasta) {
        return getDao().getByPasta(pasta);
    }
    
    private Boolean checkAcessParticipante(UsuarioLogin user, PastaRestricao restricao) {
        Boolean resp = false;
        PessoaFisica pessoaFisica = user.getPessoaFisica();
        if (pessoaFisica != null) {
            Processo processo = restricao.getPasta().getProcesso();
            ParticipanteProcesso participante = participanteProcessoManager.getParticipanteProcessoByPessoaProcesso(pessoaFisica, processo);
            if (1 == restricao.getAlvo()) {
                resp = participante != null && participante.getAtivo() && restricao.getRead();
            } else if (0 == restricao.getAlvo()) {
                resp = restricao.getRead() && (participante == null || !participante.getAtivo());
            }
        }
        return resp;
    }

}
