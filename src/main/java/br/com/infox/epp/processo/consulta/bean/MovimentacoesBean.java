package br.com.infox.epp.processo.consulta.bean;

import java.util.Date;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

public class MovimentacoesBean {

    private Papel papel;
    private UsuarioLogin usuario;
    private Localizacao localizacao;
    private Localizacao localizacaoExterna;
    private Date dataInicio;
    private Date dataFim;
    private Tarefa tarefa;
    private TaskInstance taskInstance;

    public MovimentacoesBean(ProcessoTarefa processoTarefa, UsuarioTaskInstance usuarioTaskInstance, TaskInstance taskInstance) {
        this.dataInicio = taskInstance.getCreate();
        this.dataFim = taskInstance.getEnd();
        this.setTarefa(processoTarefa.getTarefa());
        this.setTaskInstance(taskInstance);
        setUsuarioTaskInstance(usuarioTaskInstance);
    }

    private void setUsuarioTaskInstance(UsuarioTaskInstance usuarioTaskInstance) {
        if (usuarioTaskInstance == null) {
            this.papel = null;
            this.usuario = null;
            this.localizacao = null;
            this.localizacaoExterna = null;
        } else {
            this.papel = usuarioTaskInstance.getPapel();
            this.usuario = usuarioTaskInstance.getUsuario();
            this.localizacao = usuarioTaskInstance.getLocalizacao();
            this.localizacaoExterna = usuarioTaskInstance.getLocalizacaoExterna();
        }
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Localizacao getLocalizacaoExterna() {
        return localizacaoExterna;
    }

    public void setLocalizacaoExterna(Localizacao localizacaoExterna) {
        this.localizacaoExterna = localizacaoExterna;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(TaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }

}
