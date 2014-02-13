package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(PartesController.NAME)
public class PartesController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "partesController";

    private ParteProcessoEnum tipoPartes;
    private PessoaFisica pessoaFisica = new PessoaFisica();
    private PessoaJuridica pessoaJuridica = new PessoaJuridica();
    private List<Pessoa> pessoas = new ArrayList<>();

    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private PessoaJuridicaManager pessoaJuridicaManager;

    public ParteProcessoEnum getTipoPartes() {
        return tipoPartes;
    }

    public void setTipoPartes(ParteProcessoEnum tipoPartes) {
        this.tipoPartes = tipoPartes;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public void searchByCpf() {
        final String cpf = pessoaFisica.getCpf();
        setPessoaFisica(pessoaFisicaManager.getByCpf(cpf));
        if (getPessoaFisica() == null) {
            setPessoaFisica(new PessoaFisica());
            getPessoaFisica().setCpf(cpf);
            getPessoaFisica().setAtivo(true);
        }
    }

    public void searchByCnpj() {
        final String cnpj = pessoaJuridica.getCnpj();
        setPessoaJuridica(pessoaJuridicaManager.getByCnpj(cnpj));
        if (getPessoaJuridica() == null) {
            setPessoaJuridica(new PessoaJuridica());
            getPessoaJuridica().setCnpj(cnpj);
            getPessoaJuridica().setAtivo(true);
        }
    }
    
    public void includePessoaFisica(){
        try {
            pessoaFisicaManager.persist(getPessoaFisica());
            pessoas.add(getPessoaFisica());
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            setPessoaFisica(new PessoaFisica());
        }
    }
    
    public void includePessoaJuridica(){
        try {
            pessoaJuridicaManager.persist(getPessoaJuridica());
            pessoas.add(getPessoaJuridica());
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            setPessoaJuridica(new PessoaJuridica());
        }
    }
    
    public TipoPessoaEnum[] getTipoPessoaItens() {
        return TipoPessoaEnum.values();
    }
    
    public boolean apenasPessoaFisica(){
        return ParteProcessoEnum.F.equals(tipoPartes);
    }
    
    public boolean apenasPessoaJuridica(){
        return ParteProcessoEnum.J.equals(tipoPartes);
    }

}
