package br.com.infox.epp.processo.documento.assinatura;

import java.io.Serializable;
import java.util.Date;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

public class AssinaturaDocumento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idAssinatura;
    private UsuarioLogin usuario;
    private String nomeUsuario;
    private String nomePapel;
    private String nomeLocalizacao;
    private String md5Documento;
    private Date dataAssinatura;
    private String signature;
    private String certChain;
    private ProcessoDocumentoBin processoDocumentoBin;
    
    public AssinaturaDocumento() {
    }

    public int getIdAssinatura() {
        return idAssinatura;
    }

    public void setIdAssinatura(int idAssinatura) {
        this.idAssinatura = idAssinatura;
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getMd5Documento() {
        return md5Documento;
    }

    public void setMd5Documento(String md5Documento) {
        this.md5Documento = md5Documento;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return processoDocumentoBin;
    }

    public void setProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    public String getNomePapel() {
        return nomePapel;
    }

    public void setNomePapel(String nomePapel) {
        this.nomePapel = nomePapel;
    }

    public String getNomeLocalizacao() {
        return nomeLocalizacao;
    }

    public void setNomeLocalizacao(String nomeLocalizacao) {
        this.nomeLocalizacao = nomeLocalizacao;
    }
    
}