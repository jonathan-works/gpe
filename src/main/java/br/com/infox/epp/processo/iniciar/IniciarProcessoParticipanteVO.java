package br.com.infox.epp.processo.iniciar;

import java.util.Date;

import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.jsf.converter.CnpjConverter;
import br.com.infox.jsf.converter.CpfConverter;

public class IniciarProcessoParticipanteVO {
    
    private String id;
    private TipoPessoaEnum tipoPessoa = TipoPessoaEnum.F;
    private String codigo;
    private String nome;
    private String email;
    private String razaoSocial;
    private Date dataNascimento;
    private TipoParte tipoParte;
    private IniciarProcessoParticipanteVO participanteSuperior;
    private Date dataInicio;
    private Date dataFim;
    private String caminhoAbsoluto = "";
    
    private Integer idPessoa;
    private Integer idMeioContato;
    
    public void adicionar() {
        id = codigo;
        id += tipoParte.getId();
        if (participanteSuperior != null) {
            id += participanteSuperior.getId();
            this.caminhoAbsoluto = participanteSuperior.getCaminhoAbsoluto() + "|" + getId();
        } else {
            this.caminhoAbsoluto = getId();
        }
    }
    
    public void loadPessoaFisica(PessoaFisica pessoaFisica) {
        loadPessoa(pessoaFisica);
        this.dataNascimento = pessoaFisica.getDataNascimento();
    }
    
    public void loadPessoaJuridica(PessoaJuridica pessoaJuridica) {
        loadPessoa(pessoaJuridica);
        this.razaoSocial = pessoaJuridica.getRazaoSocial();
    }
    
    public void loadMeioContato(MeioContato meioContato) {
        this.email = meioContato.getMeioContato();
        this.idMeioContato = meioContato.getIdMeioContato();
    }
    
    private void loadPessoa(Pessoa pessoa) {
        this.codigo = pessoa.getCodigo();
        this.nome = pessoa.getNome();
        this.idPessoa = pessoa.getIdPessoa();
    }
    
    public void limparDadosPessoaFisica() {
        if (idPessoa != null) {
            this.dataNascimento = null;
        }
        if (idMeioContato != null) {
            this.email = null;
            this.idMeioContato = null;
        }
        limparDadosPessoa();
    }
    
    public void limparDadosPessoaJuridica() {
        if (idPessoa != null) {
            this.razaoSocial = null;
        }
        limparDadosPessoa();
    }
    
    public void limparDadosPessoa() {
        if (idPessoa != null) {
            this.nome = null;
            this.codigo = null;
            this.idPessoa = null;
        }
    }
    
    public String getId() {
        return id;
    }

    public TipoPessoaEnum getTipoPessoa() {
        return tipoPessoa;
    }
    
    public void setTipoPessoa(TipoPessoaEnum tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRazaoSocial() {
        return razaoSocial;
    }
    
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    
    public Date getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public TipoParte getTipoParte() {
        return tipoParte;
    }
    
    public void setTipoParte(TipoParte tipoParte) {
        this.tipoParte = tipoParte;
    }
    
    public IniciarProcessoParticipanteVO getParticipanteSuperior() {
        return participanteSuperior;
    }

    public void setParticipanteSuperior(IniciarProcessoParticipanteVO participanteSuperior) {
        this.participanteSuperior = participanteSuperior;
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
    
    public String getCaminhoAbsoluto() {
        return caminhoAbsoluto;
    }

    public boolean isPessoaLoaded() {
        return idPessoa != null;
    }
    
    public boolean isMeioContatoLoaded() {
        return idMeioContato != null;
    }
    
    public String getCodigoFormatado() {
        if (TipoPessoaEnum.F.equals(tipoPessoa)) {
            return CpfConverter.format(codigo);
        } else if (TipoPessoaEnum.J.equals(tipoPessoa)) {
            return CnpjConverter.format(codigo);
        } else {
            return codigo;
        }
    }
    
}
