package br.com.infox.epp.processo.documento;

import org.joda.time.DateTime;

import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;

public class AssinaturaDto {

    private String cpf;
    private String nome;
    private String perfil;
    private String dataAssinatura;
    
    public AssinaturaDto() {
    }
    
    public AssinaturaDto(AssinaturaDocumento assinaturaDocumento) {
        this.cpf = assinaturaDocumento.getPessoaFisica().getCpf();
        this.nome = assinaturaDocumento.getNomeUsuario();
        this.perfil = assinaturaDocumento.getNomeUsuarioPerfil();
        this.dataAssinatura = new DateTime(assinaturaDocumento.getDataAssinatura()).toString("yyyy-MM-dd");
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getDataAssinatura() {
        return dataAssinatura;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
        result = prime * result + ((perfil == null) ? 0 : perfil.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssinaturaDto other = (AssinaturaDto) obj;
        if (cpf == null) {
            if (other.cpf != null)
                return false;
        } else if (!cpf.equals(other.cpf))
            return false;
        if (perfil == null) {
            if (other.perfil != null)
                return false;
        } else if (!perfil.equals(other.perfil))
            return false;
        return true;
    }

}
