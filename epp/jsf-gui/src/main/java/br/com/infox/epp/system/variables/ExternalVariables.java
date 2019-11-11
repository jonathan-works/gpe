package br.com.infox.epp.system.variables;

import static br.com.infox.epp.access.api.Authenticator.getLocalizacaoAtual;
import static br.com.infox.epp.access.api.Authenticator.getPapelAtual;
import static br.com.infox.epp.access.api.Authenticator.getUsuarioPerfilAtual;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.meiocontato.dao.MeioContatoDAO;
import br.com.infox.epp.meiocontato.entity.MeioContato;
import br.com.infox.epp.meiocontato.type.TipoMeioContatoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.util.time.DateWrapper;

@Name(ExternalVariables.NAME)
public class ExternalVariables implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private static final String PARTES_PROCESSO_ATUAL = "partesProcessoAtual";
    private static final String PAPEL_USUARIO_LOGADO = "papelUsuarioLogado";
    private static final String NUMERO_PROCESSO_ATUAL = "numeroProcessoAtual";
    private static final String USUARIO_LOGADO = "usuarioLogado";
    private static final String CPF_PESSOA_LOGADA = "CPF_pessoa_logada";
    private static final String DATA_NASCIMENTO_PESSOA_LOGADA = "Data_nascimento_pessoa_logada";
    private static final String TELEFONE_PESSOA_LOGADA = "Telefone_pessoa_logada";
    private static final String LOCALIZACAO_USUARIO_LOGADO = "localizacaoUsuarioLogado";
    private static final String PERFIL_USUARIO_LOGADO = "perfilUsuarioLogado";
    private static final String DATA_ATUAL = "dataAtual";
    private static final String DATA_ATUAL_FORMATADA = "dataAtualFormatada";
    private static final String EMAIL_USUARIO_LOGADO = "emailUsuarioLogado";

    public static final String NAME = "externalVariables";

    @Factory(DATA_ATUAL_FORMATADA)
    public String getDataAtualFormatada() {
        return getCurrentDateFormattedValue(DateFormat.FULL);
    }

    @Factory(DATA_ATUAL)
    public String getDataAtual() {
        return getCurrentDateFormattedValue(DateFormat.MEDIUM);
    }

    @Factory(USUARIO_LOGADO)
    public String getUsuarioLogado() {
        return extractObjectStringValue(Authenticator.getUsuarioLogado());
    }
    
    @Factory(CPF_PESSOA_LOGADA) 
    public String getCpfPessoaLogada() {
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	if (usuarioLogado != null && usuarioLogado.getPessoaFisica() != null) {
    		return usuarioLogado.getPessoaFisica().getCodigoFormatado();
    	} else {
    		return "-";
    	}
    }
    
    @Factory(DATA_NASCIMENTO_PESSOA_LOGADA) 
    public String getDataNascimentoPessoaLogada() {
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	if (usuarioLogado != null && usuarioLogado.getPessoaFisica() != null && usuarioLogado.getPessoaFisica().getDataNascimento() != null) {
    		Date dataNascimento = usuarioLogado.getPessoaFisica().getDataNascimento();
    		return new DateWrapper(dataNascimento).toString("dd/MM/yyyy");
    	} else {
    		return "-";
    	}
    }
    
    @Factory(TELEFONE_PESSOA_LOGADA) 
    public String getTelefonePessoaLogada() {
    	UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
    	if (usuarioLogado != null && usuarioLogado.getPessoaFisica() != null) {
    		MeioContato telefone = getMeioContatoDAO().getMeioContatoByPessoaAndTipo(usuarioLogado.getPessoaFisica(), TipoMeioContatoEnum.TF);
    		if (telefone != null) {
    			return telefone.getMeioContato();
    		} else {
    			telefone = getMeioContatoDAO().getMeioContatoByPessoaAndTipo(usuarioLogado.getPessoaFisica(), TipoMeioContatoEnum.TM);
    			if (telefone != null) {
    				return telefone.getMeioContato();
    			} else {
    				return "-";
    			}
    		}
     	} else {
    		return "-";
    	}
    }
    
    @Factory(LOCALIZACAO_USUARIO_LOGADO)
    public String getLocalizacaoUsuarioLogado() {
        return extractObjectStringValue(getLocalizacaoAtual());
    }
    
    @Factory(PAPEL_USUARIO_LOGADO)
    public String getPapelUsuarioLogado() {
        return extractObjectStringValue(getPapelAtual());
    }

    @Factory(PERFIL_USUARIO_LOGADO)
    public String getPerfilUsuarioLogado() {
        return extractObjectStringValue(getUsuarioPerfilAtual());
    }
    
    @Factory(EMAIL_USUARIO_LOGADO)
    public String getEmailUsuarioLogado() {
        try {
            return Authenticator.getUsuarioLogado().getEmail();
        } catch (NullPointerException e){
            return "-";
        }
    }
    
    @Factory(NUMERO_PROCESSO_ATUAL)
    public String getNumeroProcessoAtual() {
        final Processo processo = ((ProcessoEpaHome) Component
                .getInstance(ProcessoEpaHome.NAME)).getInstance();
        String result;
        if (processo == null) {
            result = "-";
        } else {
            result = processo.getNumeroProcesso();
        }
        return result;
    }
    
    @Factory(PARTES_PROCESSO_ATUAL)
    public String getPartesProcessoAtual() {
        final Processo processo = (Processo) ((ProcessoEpaHome) Component
                .getInstance(ProcessoEpaHome.NAME)).getInstance();
        String result;
        if (processo == null) {
            result = "-";
        } else {
            result = extractObjectStringValue(processo.getParticipantes());
        }
        return result;
    }

    private String extractObjectStringValue(Object object) {
        String result;
        if (object == null) {
            result = "-";
        } else {
            result = object.toString();
        }
        return result;
    }

    private String getCurrentDateFormattedValue(final int style) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(GregorianCalendar.MILLISECOND, 0);
        gregorianCalendar.set(GregorianCalendar.SECOND, 0);
        gregorianCalendar.set(GregorianCalendar.MINUTE, 0);
        gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        return DateFormat.getDateInstance(style).format(gregorianCalendar.getTime());
    }
    
    private MeioContatoDAO getMeioContatoDAO() {
    	return Beans.getReference(MeioContatoDAO.class);
    }

}
