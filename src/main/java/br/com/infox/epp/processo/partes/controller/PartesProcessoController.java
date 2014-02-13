package br.com.infox.epp.processo.partes.controller;

import org.jboss.seam.annotations.Name;

import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(PartesProcessoController.NAME)
public class PartesProcessoController  extends AbstractPartesController{
    
    public static final String NAME = "partesProcessoController";
    
    private ProcessoEpa processoEpa;

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
        setPessoaFisica(new PessoaFisica());
        setPessoaJuridica(new PessoaJuridica());
    }
    
    private Natureza getNatureza(){
        return processoEpa.getNaturezaCategoriaFluxo().getNatureza();
    }

    @Override
    public void includePessoaFisica() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void includePessoaJuridica() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
    }

    @Override
    public void removePessoa(Pessoa pessoa) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean podeAdicionarPartes(){
        return getNatureza().getHasPartes() && (getNatureza().getNumeroPartes() == 0 || processoEpa.getPartes().size() < getNatureza().getNumeroPartes());
    }

}
