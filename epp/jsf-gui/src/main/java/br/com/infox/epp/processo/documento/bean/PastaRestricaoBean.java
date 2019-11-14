package br.com.infox.epp.processo.documento.bean;

public class PastaRestricaoBean {

    private Boolean read;
    private Boolean write;
    private Boolean delete;
    private Boolean logicDelete;

    public PastaRestricaoBean() {
        this.read = false;
        this.write = false;
        this.delete = false;
        this.logicDelete = false;
    }
    
    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }

    public void setWrite(Boolean write) {
        this.write = write;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        this.logicDelete = logicDelete;
    }
}