package br.com.infox.epp.processo.iniciar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.jsf.converter.CnpjConverter;
import br.com.infox.jsf.converter.CpfConverter;

public class IniciarProcessoParticipanteVO extends DefaultTreeNode implements Comparable<IniciarProcessoParticipanteVO> {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private TipoPessoaEnum tipoPessoa = TipoPessoaEnum.F;
    private String codigo;
    private String nome;
    private String email;
    private String razaoSocial;
    private Date dataNascimento;
    private TipoParte tipoParte;
    private Date dataInicio;
    private Date dataFim;
    
    private int nivel;
    private Pessoa pessoa;
    private MeioContato meioContato;
    
    public IniciarProcessoParticipanteVO() {
        super();
        setData(this);
    }
    
    public List<ParticipanteProcesso> getListParticipantes(Processo processo) {
        List<ParticipanteProcesso> participantes = new ArrayList<>();
        ParticipanteProcesso participante = createParticipanteProcesso(null, processo);
        participantes.add(participante);
        resolveChildren(participantes, this.getChildren(), participante, processo);
        return participantes;
    }
    
    private void resolveChildren(List<ParticipanteProcesso> participantes, List<TreeNode> children, ParticipanteProcesso participantePai, Processo processo) {
        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {
                IniciarProcessoParticipanteVO participanteVO = (IniciarProcessoParticipanteVO) treeNode;
                ParticipanteProcesso participanteProcesso = participanteVO.createParticipanteProcesso(participantePai, processo);
                participantes.add(participanteProcesso);
                resolveChildren(participantes, treeNode.getChildren(), participanteProcesso, processo);
            }
        }
    }
    
    private ParticipanteProcesso createParticipanteProcesso(ParticipanteProcesso participantePai, Processo processo) {
        ParticipanteProcesso participanteProcesso = new ParticipanteProcesso();
        participanteProcesso.setNome(nome);
        participanteProcesso.setParticipantePai(participantePai);
        participanteProcesso.setTipoParte(tipoParte);
        participanteProcesso.setDataInicio(dataInicio);
        participanteProcesso.setDataFim(dataFim);
        participanteProcesso.setAtivo(true);
        participanteProcesso.setProcesso(processo);
        if (TipoPessoaEnum.F.equals(tipoPessoa)) {
            PessoaFisica pessoaFisica = null;
            if (isPessoaLoaded()) {
                pessoaFisica = (PessoaFisica) pessoa;
                if (pessoaFisica.getDataNascimento() == null && dataNascimento != null) {
                    pessoaFisica.setDataNascimento(dataNascimento);
                }
            } else {
                pessoaFisica = createPessoaFisica();
            }
            if (isMeioContatoLoaded()) {
                if (StringUtil.isEmpty(meioContato.getMeioContato()) && email != null) {
                    meioContato.setMeioContato(email);
                }
            } else if (!StringUtil.isEmpty(email)){
                MeioContato meioContato = new MeioContato(TipoMeioContatoEnum.EM);
                meioContato.setPessoa(pessoaFisica);
                meioContato.setMeioContato(email);
                pessoaFisica.getMeioContatoList().add(meioContato);
            }
            participanteProcesso.setPessoa(pessoaFisica);
        } else if (TipoPessoaEnum.J.equals(tipoPessoa)) {
            PessoaJuridica pessoaJuridica = null;
            if (isPessoaLoaded()) {
                pessoaJuridica = (PessoaJuridica) pessoa;
                if (pessoaJuridica.getRazaoSocial() == null && razaoSocial != null) {
                    pessoaJuridica.setRazaoSocial(razaoSocial);
                }
            } else {
                pessoaJuridica = createPessoaJuridica();
            }
            participanteProcesso.setPessoa(pessoaJuridica);
        }
        return participanteProcesso;
    }

    private PessoaFisica createPessoaFisica() {
        PessoaFisica pessoaFisica = new PessoaFisica();
        pessoaFisica.setCpf(codigo);
        pessoaFisica.setNome(nome);
        pessoaFisica.setAtivo(true);
        pessoaFisica.setDataNascimento(dataNascimento);
        return pessoaFisica;
    }
    
    private PessoaJuridica createPessoaJuridica() {
        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setAtivo(true);
        pessoaJuridica.setCnpj(codigo);
        pessoaJuridica.setNome(nome);
        pessoaJuridica.setRazaoSocial(razaoSocial);
        return pessoaJuridica;
    }
    
    public void adicionar() {
        if (getParent() != null) {
            getParent().getChildren().add(this);
        }
    }
    
    public void generateId() {
        if (getParent() != null) {
            id = ((IniciarProcessoParticipanteVO) getParent()).getId();
            nivel = ((IniciarProcessoParticipanteVO) getParent()).getNivel() + 1;
        } else {
            id = "";
            nivel = 1;
        }
        id += codigo + tipoParte.getId();
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
        this.email = meioContato == null ? null : meioContato.getMeioContato();
        this.meioContato = meioContato;
    }
    
    private void loadPessoa(Pessoa pessoa) {
        this.codigo = pessoa.getCodigo();
        this.nome = pessoa.getNome();
        this.pessoa = pessoa;
    }
    
    public void limparDadosPessoaFisica() {
        if (isPessoaLoaded()) {
            this.dataNascimento = null;
        }
        if (isMeioContatoLoaded()) {
            this.email = null;
            this.meioContato = null;
        }
        limparDadosPessoa();
    }
    
    public void limparDadosPessoaJuridica() {
        if (isPessoaLoaded()) {
            this.razaoSocial = null;
        }
        limparDadosPessoa();
    }
    
    public void limparDadosPessoa() {
        if (isPessoaLoaded()) {
            this.nome = null;
            this.codigo = null;
            this.pessoa = null;
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
    
    public Pessoa getPessoa() {
        return pessoa;
    }

    public MeioContato getMeioContato() {
        return meioContato;
    }

    public boolean isPessoaLoaded() {
        return pessoa != null;
    }
    
    public boolean isMeioContatoLoaded() {
        return meioContato != null;
    }
    
    public int getNivel() {
        return nivel;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof IniciarProcessoParticipanteVO))
            return false;
        IniciarProcessoParticipanteVO other = (IniciarProcessoParticipanteVO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int compareTo(IniciarProcessoParticipanteVO o) {
        return this.getId().compareTo(o.getId());
    }
    
}
