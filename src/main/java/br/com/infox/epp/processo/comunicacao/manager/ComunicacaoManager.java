package br.com.infox.epp.processo.comunicacao.manager;

import java.util.Date;
import java.util.List;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.comunicacao.Comunicacao;

public class ComunicacaoManager extends Manager<GenericDAO, Object> {
    private static final long serialVersionUID = 1L;
    
    public List<Comunicacao> getComunicacoesAguardandoCumprimento() {
        // TODO criar método para retornar comunicações aguardando cumprimento
        return null;
    }
    
    public List<Comunicacao> getComunicacoesAguardandoCiencia() {
        // TODO criar método para retornar comunicações aguardando cumprimento
        return null;
    }

    public void setDataFimPrazo(Comunicacao comunicacao, Date prazo) {
        // TODO criar método para setar a dataFimPrazo que deve ser uma variável da comunicação
    }

    public Date getDataFimPrazoCiencia(Comunicacao comunicacao) {
        // TODO criar método para pegar a dataFimPrazo que deve ser uma variável da comunicação
        return null;
    }
    
}