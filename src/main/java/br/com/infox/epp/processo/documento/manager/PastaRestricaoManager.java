package br.com.infox.epp.processo.documento.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.fluxo.manager.ModeloPastaRestricaoManager;
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
@Stateless
public class PastaRestricaoManager extends Manager<PastaRestricaoDAO, PastaRestricao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaRestricaoManager";
    
    @In
    private ParticipanteProcessoManager participanteProcessoManager;
    @In
    private PastaManager pastaManager;
    @In
    private PapelManager papelManager;
    @In
    private LocalizacaoManager localizacaoManager;
    @In
    private ModeloPastaRestricaoManager modeloPastaRestricaoManager;

    public Map<Integer, PastaRestricaoBean> loadRestricoes(Processo processo, UsuarioLogin usuario, Localizacao localizacao, Papel papel) throws DAOException {
        List<Pasta> pastas = pastaManager.getByProcesso(processo);
        return loadRestricoes(pastas, usuario, localizacao, papel);
    }

    public Map<Integer, PastaRestricaoBean> loadRestricoes(List<Pasta> pastas, UsuarioLogin usuario, Localizacao localizacao, Papel papel) throws DAOException {
        Map<Integer, PastaRestricaoBean> restricoes = new HashMap<>();
        for (Pasta pasta : pastas) {
            PastaRestricaoBean restricaoBean = new PastaRestricaoBean();
            List<PastaRestricao> restricoesDaPasta = getByPasta(pasta);
            PastaRestricao restricaoDefault = null;
            Boolean usuarioComRestricao = false;
            
            for (PastaRestricao restricao : restricoesDaPasta) {
                PastaRestricaoEnum tipoRestricao = restricao.getTipoPastaRestricao();
                
                if (PastaRestricaoEnum.P.equals(tipoRestricao)) {
                    Boolean temRestricaoPapel = populateBeanPapel(restricaoBean, restricao, papel);
                    usuarioComRestricao = usuarioComRestricao || temRestricaoPapel;
                } else if (PastaRestricaoEnum.R.equals(tipoRestricao)) {
                    Boolean temRestricaoParticipante = populateBeanParticipante(restricaoBean, restricao, usuario);
                    usuarioComRestricao = usuarioComRestricao || temRestricaoParticipante;
                } else if (PastaRestricaoEnum.L.equals(tipoRestricao)) {
                    Boolean temRestricaoLocalizacao = populateBeanLocalizacao(restricaoBean, restricao, localizacao);
                    usuarioComRestricao = usuarioComRestricao || temRestricaoLocalizacao;
                } else if (PastaRestricaoEnum.D.equals(tipoRestricao)) {
                    restricaoDefault = restricao;
                }
            }
            if (!usuarioComRestricao && restricaoDefault != null) {
                populateBeanDefault(restricaoBean, restricaoDefault);
            }
            restricoes.put(pasta.getId(), restricaoBean);
        }
        return restricoes;
    }
    
    /**
     * Método chamado quando é encontrada uma restrição do tipo Papel.
     * 
     * Considera o alvo como o id de um papel.
     * Verifica se o papel passado por parâmetro está dentro dos papeis membros do alvo.
     * 
     * @param restricaoBean bean que está sendo populado
     * @param restricao restrição a ser analizada
     * @param papelUsuario papel a ser verificado
     * @return true se esta restrição afeta o papel informado, false caso contrário
     */
    private boolean populateBeanPapel(PastaRestricaoBean restricaoBean, PastaRestricao restricao, Papel papelUsuario) {
        Papel papelAlvo = papelManager.find(restricao.getAlvo());
        if (papelUsuario.equals(papelAlvo) || papelManager.isPapelHerdeiro(papelUsuario.getIdentificador(), papelAlvo.getIdentificador())) {
            restricaoBean.setRead(restricaoBean.getRead() || restricao.getRead());
            restricaoBean.setWrite(restricaoBean.getWrite() || restricao.getWrite());
            restricaoBean.setDelete(restricaoBean.getDelete() || restricao.getDelete());
            restricaoBean.setLogicDelete(restricaoBean.getLogicDelete() || restricao.getLogicDelete());
            return true;
        }
        return false;
    }

    /**
     * Método chamado quando é encontrada uma restrição do tipo Participante
     * 
     * Caso o alvo seja 1, a restrição especificada se refere aos participantes,
     * Caso o alvo seja 0, a restrição especificada se refere aos não participantes
     * 
     * @param restricaoBean bean que está sendo populado
     * @param restricao restrição a ser analizada
     * @param usuarioLogin usuário a ser verificado
     * @return true se esta restrição afeta o usuário informado, false caso contrário
     */
    private boolean populateBeanParticipante(PastaRestricaoBean restricaoBean, PastaRestricao restricao, UsuarioLogin usuarioLogin) {
        PessoaFisica pessoaFisica = usuarioLogin.getPessoaFisica();
        if (pessoaFisica != null) {
            ParticipanteProcesso pp = participanteProcessoManager.getParticipanteProcessoByPessoaProcesso(pessoaFisica, restricao.getPasta().getProcesso());
            Boolean ppAtivo = pp != null && pp.getAtivo();
            if (1 == restricao.getAlvo() && ppAtivo) {
                restricaoBean.setRead(restricaoBean.getRead() || restricao.getRead());
                restricaoBean.setWrite(restricaoBean.getWrite() || restricao.getWrite());
                restricaoBean.setDelete(restricaoBean.getDelete() || restricao.getDelete());
                restricaoBean.setLogicDelete(restricaoBean.getLogicDelete() || restricao.getLogicDelete());
                return true;
            } else if (0 == restricao.getAlvo() && !ppAtivo) {
                restricaoBean.setRead(restricaoBean.getRead() || restricao.getRead());
                restricaoBean.setWrite(restricaoBean.getWrite() || restricao.getWrite());
                restricaoBean.setDelete(restricaoBean.getDelete() || restricao.getDelete());
                restricaoBean.setLogicDelete(restricaoBean.getLogicDelete() || restricao.getLogicDelete());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Método chamado quando é encontrada uma restrição do tipo Localização
     * 
     * Considera o alvo como o id de uma Localização
     * Verifica se a localização passada por parâmetro é igual a localização alvo considerando
     * a estrutura de árvore de localizações.
     * 
     * @param restricaoBean bean que está sendo populado
     * @param restricao restrição a ser analizada
     * @param localizacao localização a ser verificada
     * @return true, se a restrição afeta esta localização, falso caso contrário
     */
    private boolean populateBeanLocalizacao(PastaRestricaoBean restricaoBean, PastaRestricao restricao, Localizacao localizacao) {
        Localizacao localizacaoAlvo = localizacaoManager.find(restricao.getAlvo());
        if (localizacao.equals(localizacaoAlvo)) {
            restricaoBean.setRead(restricaoBean.getRead() || restricao.getRead());
            restricaoBean.setWrite(restricaoBean.getWrite() || restricao.getWrite());
            restricaoBean.setDelete(restricaoBean.getDelete() || restricao.getDelete());
            restricaoBean.setLogicDelete(restricaoBean.getLogicDelete() || restricao.getLogicDelete());
            return true;
        }
        return false;
    }

    /**
     * Método chamado quando o usuário não foi afetado por nenhuma restrição específica
     * 
     * @param restricaoBean
     * @param restricaoDefault
     */
    private void populateBeanDefault(PastaRestricaoBean restricaoBean, PastaRestricao restricaoDefault) {
        restricaoBean.setRead(restricaoDefault.getRead());
        restricaoBean.setWrite(restricaoDefault.getWrite());
        restricaoBean.setDelete(restricaoDefault.getDelete());
        restricaoBean.setLogicDelete(restricaoDefault.getLogicDelete());
    }

    public List<PastaRestricao> getByPasta(Pasta pasta) {
        return getDao().getByPasta(pasta);
    }

    public void deleteByPasta(Pasta pasta) throws DAOException {
        getDao().deleteByPasta(pasta);
    }
    
    public PastaRestricao persistRestricaoDefault(Pasta pasta) throws DAOException {
        PastaRestricao restricao = new PastaRestricao();
        restricao.setPasta(pasta);
        restricao.setTipoPastaRestricao(PastaRestricaoEnum.D);
        restricao.setAlvo(null);
        restricao.setRead(Boolean.TRUE);
        restricao.setWrite(Boolean.TRUE);
        restricao.setDelete(Boolean.TRUE);
        restricao.setLogicDelete(Boolean.FALSE);
        return persist(restricao);
    }
    
    public void createRestricoesFromModelo(ModeloPasta modeloPasta, Pasta pasta) throws DAOException {
        List<ModeloPastaRestricao> modeloRestricaoList = modeloPastaRestricaoManager.getByModeloPasta(modeloPasta);
        for (ModeloPastaRestricao modeloRestricao : modeloRestricaoList) {
            PastaRestricao restricao = new PastaRestricao();
            restricao.setPasta(pasta);
            restricao.setTipoPastaRestricao(modeloRestricao.getTipoPastaRestricao());
            restricao.setAlvo(modeloRestricao.getAlvo());
            restricao.setRead(modeloRestricao.getRead());
            restricao.setWrite(modeloRestricao.getWrite());
            restricao.setDelete(modeloRestricao.getDelete());
            restricao.setLogicDelete(modeloRestricao.getLogicDelete());
            persist(restricao);
        }
    }

    public List<PastaRestricao> copyRestricoes(Pasta from, Pasta to) throws CloneNotSupportedException, DAOException {
        List<PastaRestricao> restricoesAntigas = getByPasta(from);
        List<PastaRestricao> restricoesNovas = new ArrayList<>(restricoesAntigas.size());
        for (PastaRestricao restricaoAntiga : restricoesAntigas) {
            PastaRestricao restricaoNova = restricaoAntiga.makeCopy();
            restricaoNova.setPasta(to);
            persist(restricaoNova);
            restricoesNovas.add(restricaoNova);
        }
        return restricoesNovas;
    }
    
    public PastaRestricao getByPastaAlvoTipoRestricao(Pasta pasta, Integer alvo, PastaRestricaoEnum tipoRestricao) {
    	return getDao().getByPastaAlvoTipoRestricao(pasta, alvo, tipoRestricao);
    }
}
