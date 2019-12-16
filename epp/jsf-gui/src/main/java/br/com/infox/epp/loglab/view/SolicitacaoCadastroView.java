package br.com.infox.epp.loglab.view;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.loglab.service.SolicitacaoCadastroService;
import br.com.infox.epp.loglab.vo.SolicitacaoCadastroVO;
import br.com.infox.epp.municipio.Estado;
import br.com.infox.epp.municipio.EstadoSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class SolicitacaoCadastroView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Getter
    @Setter
    private SolicitacaoCadastroVO instance = new SolicitacaoCadastroVO();
    
    @Getter
    @Setter
    private Estado estado = null;
    
    @Inject
    private EstadoSearch estadoSearch;
    
    @Inject
    private SolicitacaoCadastroService solicitacaoCadastroService;
    
    @ExceptionHandled
    public void criarSolicitacaoCadastro() {
        alterarEstado();
        solicitacaoCadastroService.criarSinalSolicitacaoCadastro(instance);
        limparCampos();
        FacesMessages.instance().add("Solicitação de Cadastro criada com sucesso");
    }
    
    private void limparCampos() {
        instance = new SolicitacaoCadastroVO();
        estado = null;
    }
    
    private void alterarEstado() {
        if(estado != null) {
            instance.setUfRg(estado.getCodigo());
        } else {
            instance.setUfRg(null);
        }
    }

    public List<Estado> getEstadosList() {
        List<Estado> estadosList = estadoSearch.findAll();
        return estadosList ;
    }
    
    public void teste() {
        solicitacaoCadastroService.teste();
    }
}
