package br.com.infox.epp.processo.comunicacao.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.comunicacao.Comunicacao;

@Name(ComunicacaoManager.NAME)
@AutoCreate
public class ComunicacaoManager extends Manager<GenericDAO, Object> {
    public static final String NAME = "comunicacaoManager";
    private static final long serialVersionUID = 1L;
    
    public List<Comunicacao> getComunicacoesAguardandoCumprimento() {
        // TODO criar método para retornar comunicações aguardando cumprimento
        return null;
    }
    
    public List<Comunicacao> getComunicacoesAguardandoCiencia() {
        // TODO criar método para retornar comunicações aguardando cumprimento
        return null;
    }
    
    public Integer getQtdDiasCumprimento(Comunicacao comunicacao) {
        // TODO criar método para retornar a quantidade de dias para cumprimento
        return null;
    }
    
    public Date getDataFimPrazoCiencia(Comunicacao comunicacao) {
        // TODO criar método para pegar a dataFimPrazo que deve ser uma variável da comunicação
        return null;
    }

    public void setDataFimPrazoCiencia(Comunicacao comunicacao, Date prazo) {
        // TODO criar método para setar a dataFimPrazo que deve ser uma variável da comunicação
    }

    public void getDataFimPrazoCumprimento(Comunicacao comunicacao) {
        // TODO criar método para retornar prazo de cumprimento
    }

    public void setDataFimPrazoCumprimento(Comunicacao comunicacao, Date prazo) {
        // TODO criar método para setar prazo de cumprimento
    }
}