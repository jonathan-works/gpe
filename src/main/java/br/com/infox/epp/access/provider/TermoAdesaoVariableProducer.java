package br.com.infox.epp.access.provider;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

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
import br.com.infox.epp.meiocontato.manager.MeioContatoManager;
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
        return getPessoaFisicaUsuarioLogado().getNome();
    }

    @Produces
    @Named(CPF)
    public String getCpf() {
        return getPessoaFisicaUsuarioLogado().getCodigoFormatado();
    }

    @Produces
    @Named(EMAIL)
    public String getEmail() {
        return getUsuarioLogado().getEmail();
    }

    @Produces
    @Named(DATA_NASCIMENTO)
    public String getDataNascimento() {
        return convertDateToString(getPessoaFisicaUsuarioLogado().getDataNascimento());
    }

    @Produces
    @Named(IDENTIDADE)
    public String getIdentidade() {
        return pessoaDocumentoManager.getPessoaDocumentoByPessoaTipoDocumento(getPessoaFisicaUsuarioLogado(), TipoPesssoaDocumentoEnum.CI).getDocumento();
    }

    @Produces
    @Named(ORGAO_EXPEDIDOR)
    public String getOrgaoExpedidor() {
        return pessoaDocumentoManager.getPessoaDocumentoByPessoaTipoDocumento(getPessoaFisicaUsuarioLogado(), TipoPesssoaDocumentoEnum.CI).getOrgaoEmissor();
    }

    @Produces
    @Named(DATA_EXPEDICAO)
    public String getDataExpedicao() {
        return convertDateToString(pessoaDocumentoManager.getPessoaDocumentoByPessoaTipoDocumento(getPessoaFisicaUsuarioLogado(), TipoPesssoaDocumentoEnum.CI).getDataEmissao());
    }

    @Produces
    @Named(TELEFONE_FIXO)
    public String getTelefoneFixo() {
        return meioContatoManager.getMeioContatoTelefoneFixoByPessoa(getPessoaFisicaUsuarioLogado()).getMeioContato();
    }

    @Produces
    @Named(TELEFONE_MOVEL)
    public String getTelefoneMovel() {
        return meioContatoManager.getMeioContatoTelefoneMovelByPessoa(getPessoaFisicaUsuarioLogado()).getMeioContato();
    }

    @Produces
    @Named(ESTADO_CIVIL)
    public String getEstadoCivil() {
        return getPessoaFisicaUsuarioLogado().getEstadoCivil().getLabel();
    }

    private PessoaFisica getPessoaFisicaUsuarioLogado() {
        return getUsuarioLogado().getPessoaFisica();
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
    
}