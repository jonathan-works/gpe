package br.com.infox.epp.calendario;

public class Issue {
    
    private IssueType type;
    private String issue;
    private String previousState;
    private String futureState;

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

    public static interface IssueType{}
}
