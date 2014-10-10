package br.com.infox.epp.processo.partes.controller;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.pessoa.manager.PessoaJuridicaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(PartesController.NAME)
public class PartesController extends AbstractParticipantesController {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "partesController";
    private static final LogProvider LOG = Logging.getLogProvider(PartesController.class);

    private Natureza natureza;
    private List<PessoaFisica> pessoasFisicas = new ArrayList<>();
    private List<PessoaJuridica> pessoasJuridicas = new ArrayList<PessoaJuridica>();

    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private PessoaJuridicaManager pessoaJuridicaManager;

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public List<PessoaFisica> getPessoasFisicas() {
        return pessoasFisicas;
    }

    public void setPessoasFisicas(List<PessoaFisica> pessoas) {
        this.pessoasFisicas = pessoas;
    }

    public List<PessoaJuridica> getPessoasJuridicas() {
        return pessoasJuridicas;
    }

    public void setPessoasJuridicas(List<PessoaJuridica> pessoasJuridicas) {
        this.pessoasJuridicas = pessoasJuridicas;
    }

    @Override
    public void includePessoaFisica() {
        try {
            pessoaFisicaManager.persist(getPessoaFisica());
            includePessoaFisica(getPessoaFisica());
        } catch (DAOException e) {
            LOG.error("Não foi possível inserir a pessoa " + getPessoaFisica(), e);
        } finally {
            setPessoaFisica(new PessoaFisica());
        }
    }

    @Override
    public void includePessoaJuridica() {
        try {
            pessoaJuridicaManager.persist(getPessoaJuridica());
            includePessoaJuridica(getPessoaJuridica());
        } catch (DAOException e) {
            LOG.error("Não foi possível inserir a pessoa "
                    + getPessoaJuridica(), e);
        } finally {
            setPessoaJuridica(new PessoaJuridica());
        }
    }

    @Override
    public boolean apenasPessoaFisica() {
        return ParteProcessoEnum.F.equals(getNatureza().getTipoPartes());
    }

    @Override
    public boolean apenasPessoaJuridica() {
        return ParteProcessoEnum.J.equals(getNatureza().getTipoPartes());
    }

    private void includePessoaFisica(PessoaFisica pessoa) {
        if (!pessoasFisicas.contains(pessoa)) {
            pessoasFisicas.add(pessoa);
        } else {
            FacesMessages.instance().add(Severity.WARN, pessoa
                    + "já cadastrada na lista de partes");
        }
    }

    public void removePessoaFisica(PessoaFisica pessoa) {
        pessoasFisicas.remove(pessoa);
    }

    private void includePessoaJuridica(PessoaJuridica pessoa) {
        if (!getPessoasJuridicas().contains(pessoa)) {
            getPessoasJuridicas().add(pessoa);
        } else {
            FacesMessages.instance().add(Severity.WARN, pessoa
                    + "já cadastrada na lista de partes");
        }
    }

    public void removePessoaJuridica(PessoaJuridica pessoa) {
        getPessoasJuridicas().remove(pessoa);
    }

    @Override
    public boolean podeAdicionarPartesFisicas() {
        return hasPartes()
                && !apenasPessoaJuridica()
                && (natureza.getNumeroPartesFisicas() == 0 || pessoasFisicas.size() < natureza.getNumeroPartesFisicas());
    }

    @Override
    public boolean podeAdicionarPartesJuridicas() {
        return hasPartes()
                && !apenasPessoaFisica()
                && (natureza.getNumeroPartesJuridicas() == 0 || getPessoasJuridicas().size() < natureza.getNumeroPartesJuridicas());
    }

    private boolean hasPartes() {
        return natureza != null && natureza.getHasPartes();
    }

}
