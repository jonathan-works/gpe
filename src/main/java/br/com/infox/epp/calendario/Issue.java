package br.com.infox.epp.calendario;

import br.com.infox.epp.processo.entity.Processo;

public class Issue {
    
    private IssueType type;
    private String issue;
    private String previousState;
    private String futureState;
    private Processo processo;
    private String valorAposModificaoes;

    public static interface IssueType{}

    public String getIssue() {
        return issue;
    }

    public Issue setIssue(String issue) {
        this.issue = issue;
        return this;
    }

    public String getPreviousState() {
        return previousState;
    }

    public Issue setPreviousState(String state) {
        this.previousState = state;
        return this;
    }

    public String getFutureState() {
        return futureState;
    }

    public Issue setFutureState(String futureState) {
        this.futureState = futureState;
        return this;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getValorAposModificaoes() {
        return valorAposModificaoes;
    }

    public void setValorAposModificaoes(String valorAposModificaoes) {
        this.valorAposModificaoes = valorAposModificaoes;
    }

}
