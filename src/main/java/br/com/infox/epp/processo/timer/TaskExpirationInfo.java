
package br.com.infox.epp.processo.timer;

import java.io.Serializable;
import java.util.Date;

public class TaskExpirationInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private String transition;
    private Date expiration;
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTransition() {
        return transition;
    }
    
    public void setTransition(String transition) {
        this.transition = transition;
    }
    
    public Date getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    
}
