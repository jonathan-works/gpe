package br.com.infox.epp.git;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name(GitRepository.NAME)
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class GitRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "gitRepository";
    
    private String branch;
    private String commitId;
    private String buildTime;
    private String describe;
    
    @Create
    public void init() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
        this.branch = properties.getProperty("git.branch");
        this.commitId = properties.getProperty("git.commit.id.abbrev");
        this.buildTime = properties.getProperty("git.build.time");
        this.describe = properties.getProperty("git.commit.id.describe");
    }
    
    public String getBranch() {
        return branch;
    }
    
    public String getCommitId() {
        return commitId;
    }
    
    public String getBuildTime() {
        return buildTime;
    }

    public String getDescribe() {
        return describe;
    }
}
