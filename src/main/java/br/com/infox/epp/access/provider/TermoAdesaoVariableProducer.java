package br.com.infox.epp.access.provider;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.pessoa.documento.entity.PessoaDocumento;
import br.com.infox.epp.pessoa.documento.manager.PessoaDocumentoManager;
import br.com.infox.epp.pessoa.documento.type.TipoPesssoaDocumentoEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Name(TermoAdesaoVariableProducer.NAME)
@Named(TermoAdesaoVariableProducer.NAME)
public class TermoAdesaoVariableProducer implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "termoAdesaoVariableProducer";

    private static final String NOME = "nome";
    private static final String CPF = "cpf";
    private static final String EMAIL = "email";
    private static final String DATA_NASCIMENTO = "dataNascimento";
    private static final String IDENTIDADE = "identidade";
    private static final String ORGAO_EXPEDIDOR = "orgaoExpedidor";
    private static final String DATA_EXPEDICAO = "dataExpedicao";
    private static final String TELEFONE_FIXO = "telefoneFixo";
    private static final String TELEFONE_MOVEL = "telefoneMovel";
    private static final String ESTADO_CIVIL = "estadoCivil";
    
    @Inject
    private PessoaDocumentoManager pessoaDocumentoManager;
    @Inject
    private MeioContatoManager meioContatoManager;
    
    @Produces
    @Named(NOME)
    public String getNome() {
        return getNome(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(CPF)
    public String getCpf() {
        return getCpf(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(EMAIL)
    public String getEmail() {
        return getUsuarioLogado() == null ? "-" : getUsuarioLogado().getEmail();
    }

    @Produces
    @Named(DATA_NASCIMENTO)
    public String getDataNascimento() {
        return getDataNascimento(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(IDENTIDADE)
    public String getIdentidade() {
        return getIdentidade(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(ORGAO_EXPEDIDOR)
    public String getOrgaoExpedidor() {
        return getOrgaoExpedidor(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(DATA_EXPEDICAO)
    public String getDataExpedicao() {
        return getDataExpedicao(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(TELEFONE_FIXO)
    public String getTelefoneFixo() {
        return getTelefoneFixo(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(TELEFONE_MOVEL)
    public String getTelefoneMovel() {
        return getTelefoneMovel(getPessoaFisicaUsuarioLogado());
    }

    @Produces
    @Named(ESTADO_CIVIL)
    public String getEstadoCivil() {
        return getEstadoCivil(getPessoaFisicaUsuarioLogado());
    }

    private PessoaFisica getPessoaFisicaUsuarioLogado() {
        return getUsuarioLogado() == null ? null : getUsuarioLogado().getPessoaFisica();
    }

    private UsuarioLogin getUsuarioLogado() {
        UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
        if (usuario == null) {
            return null;
        }
        return ((UsuarioLoginDAO) Component.getInstance(UsuarioLoginDAO.NAME)).find(usuario.getIdUsuarioLogin());
    }

    private String convertDateToString(Date date){
        return MessageFormat.format("{0,date,dd/MM/yyyy}", date);
    }

    public Map<String, String> getTermoAdesaoVariables(PessoaFisica pessoaFisica) {
        HashMap<String, String> variaveis = new HashMap<>();
        variaveis.put(String.format("#{%s}", NOME), getNome(pessoaFisica));
        variaveis.put(String.format("#{%s}", CPF), getCpf(pessoaFisica));
        variaveis.put(String.format("#{%s}", DATA_NASCIMENTO), getDataNascimento(pessoaFisica));
        variaveis.put(String.format("#{%s}", ESTADO_CIVIL), getEstadoCivil(pessoaFisica));
        
        variaveis.put(String.format("#{%s}", IDENTIDADE), getIdentidade(pessoaFisica));
        variaveis.put(String.format("#{%s}", ORGAO_EXPEDIDOR), getOrgaoExpedidor(pessoaFisica));
        variaveis.put(String.format("#{%s}", DATA_EXPEDICAO), getDataExpedicao(pessoaFisica));
        
        variaveis.put(String.format("#{%s}",EMAIL), getEmail(pessoaFisica));
        
        variaveis.put(String.format("#{%s}",TELEFONE_FIXO), getTelefoneFixo(pessoaFisica));
        
        variaveis.put(String.format("#{%s}",TELEFONE_MOVEL), getTelefoneMovel(pessoaFisica));
        
        return variaveis;
    }

    private String getEstadoCivil(PessoaFisica pessoaFisica) {
        return pessoaFisica == null ? "-" : pessoaFisica.getEstadoCivil().getLabel();
    }

    private String getNome(PessoaFisica pessoaFisica) {
        return pessoaFisica == null ? "-" : pessoaFisica.getNome();
    }

    private String getCpf(PessoaFisica pessoaFisica) {
        return pessoaFisica == null ? "-" : pessoaFisica.getCodigoFormatado();
    }
    
    private String getIdentidade(PessoaFisica pessoaFisica){
        PessoaDocumento identidade = getIdentidadeByPessoaFisica(pessoaFisica);
        return identidade == null ? "-" : identidade.getDocumento();
    }
    private String getOrgaoExpedidor(PessoaFisica pessoaFisica){
        PessoaDocumento identidade = getIdentidadeByPessoaFisica(pessoaFisica);
        return identidade == null ? "-" : identidade.getOrgaoEmissor();
    }
    
    private String getDataExpedicao(PessoaFisica pessoaFisica) {
        PessoaDocumento identidade = getIdentidadeByPessoaFisica(pessoaFisica);
        return identidade == null ? "-" : convertDateToString(identidade.getDataEmissao());
    }

    private PessoaDocumento getIdentidadeByPessoaFisica(PessoaFisica pessoaFisica) {
        return pessoaFisica == null ? null : pessoaDocumentoManager.getPessoaDocumentoByPessoaTipoDocumento(pessoaFisica, TipoPesssoaDocumentoEnum.CI);
    }

    private String getDataNascimento(PessoaFisica pessoaFisica) {
        return pessoaFisica == null ? "-" : convertDateToString(pessoaFisica.getDataNascimento());
    }
    
    private String getTelefoneFixo(PessoaFisica pessoaFisica){
        if (pessoaFisica == null)
            return "-";
        MeioContato telefoneMovel = meioContatoManager.getMeioContatoTelefoneFixoByPessoa(pessoaFisica);
        return telefoneMovel != null ? telefoneMovel.getMeioContato() : "-";
    }
    private String getTelefoneMovel(PessoaFisica pessoaFisica){
        if (pessoaFisica == null)
            return "-";
        MeioContato telefoneMovel = meioContatoManager.getMeioContatoTelefoneFixoByPessoa(pessoaFisica);
        return telefoneMovel != null ? telefoneMovel.getMeioContato() : "-";
    }

    private String getEmail(PessoaFisica pessoaFisica) {
        if (pessoaFisica == null)
            return "-";
        String variavelEmail="-";
        MeioContato email = pessoaFisica.getMeioContato(TipoMeioContatoEnum.EM);
        
        if (pessoaFisica.getUsuarioLogin() != null) {
            variavelEmail=pessoaFisica.getUsuarioLogin().getEmail();
        } else {
            if (email != null)
                variavelEmail=email.getMeioContato();
        }
        return variavelEmail;
    }
}