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
public class TermoAdesaoVariableProducer implements Serializable {
    private static final long serialVersionUID = 1L;

    protected static final String NOME = "nome";
    protected static final String CPF = "cpf";
    protected static final String EMAIL = "email";
    protected static final String DATA_NASCIMENTO = "dataNascimento";
    protected static final String IDENTIDADE = "identidade";
    protected static final String ORGAO_EXPEDIDOR = "orgaoExpedidor";
    protected static final String DATA_EXPEDICAO = "dataExpedicao";
    protected static final String TELEFONE_FIXO = "telefoneFixo";
    protected static final String TELEFONE_MOVEL = "telefoneMovel";
    protected static final String ESTADO_CIVIL = "estadoCivil";
    
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

    protected UsuarioLogin getUsuarioLogado() {
        UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
        if (usuario == null) {
            return null;
        }
        return ((UsuarioLoginDAO) Component.getInstance(UsuarioLoginDAO.NAME)).find(usuario.getIdUsuarioLogin());
    }

    private String convertDateToString(Date date){
        return MessageFormat.format("{0,date,dd/MM/yyyy}", date);
    }
    
    protected Map<String, String> getMapaVariaveis(PessoaFisica pessoaFisica) {
        Map<String, String> variaveis = new HashMap<>();
        
        variaveis.put(NOME, getNome(pessoaFisica));
        variaveis.put(CPF, getCpf(pessoaFisica));
        variaveis.put(DATA_NASCIMENTO, getDataNascimento(pessoaFisica));
        variaveis.put(ESTADO_CIVIL, getEstadoCivil(pessoaFisica));
        
        variaveis.put(IDENTIDADE, getIdentidade(pessoaFisica));
        variaveis.put(ORGAO_EXPEDIDOR, getOrgaoExpedidor(pessoaFisica));
        variaveis.put(DATA_EXPEDICAO, getDataExpedicao(pessoaFisica));
        
        variaveis.put(EMAIL, getEmail(pessoaFisica));
        
        variaveis.put(TELEFONE_FIXO, getTelefoneFixo(pessoaFisica));
        
        variaveis.put(TELEFONE_MOVEL, getTelefoneMovel(pessoaFisica));
        
        return variaveis;    	
    }

    public Map<String, String> getTermoAdesaoVariables(PessoaFisica pessoaFisica) {
        Map<String, String> variaveis = getMapaVariaveis(pessoaFisica);
        
        Map<String, String> mapaELs = new HashMap<>();
        
        for(String variavel : variaveis.keySet()) {
        	String valor = variaveis.get(variavel);
        	mapaELs.put(String.format("#{%s}", variavel), valor);
        }
        return mapaELs;
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