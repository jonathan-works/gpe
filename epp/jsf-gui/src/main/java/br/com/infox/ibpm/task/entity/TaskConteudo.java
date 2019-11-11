package br.com.infox.ibpm.task.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name=TaskConteudo.TABLE_NAME)
@Indexed(index="IndexTaskContent")
public class TaskConteudo {

    public static final String TABLE_NAME = "tb_task_conteudo_index";
    
    private Long idTaskInstance;
    private String conteudo;

    private Integer numeroProcesso;

    @Id
    @Column(name="id_taskinstance", unique=true, nullable=false)
    @NotNull
    public Long getIdTaskInstance() {
        return idTaskInstance;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    @Column(name="ds_conteudo")
    @Field(index = Index.YES, store = Store.NO, name = "conteudo")
    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    @Column(name="nr_processo", nullable=false)
    public Integer getNumeroProcesso() {
        return numeroProcesso;
    }
    
    public void setNumeroProcesso(Integer numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

}
