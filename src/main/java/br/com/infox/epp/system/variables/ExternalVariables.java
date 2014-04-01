package br.com.infox.epp.system.variables;

import static br.com.infox.epp.access.api.Authenticator.getLocalizacaoAtual;
import static br.com.infox.epp.access.api.Authenticator.getUsuarioLocalizacaoAtual;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.api.Authenticator;

@Name(ExternalVariables.NAME)
@Scope(ScopeType.EVENT)
public class ExternalVariables implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String USUARIO_LOGADO = "usuarioLogado";
    private static final String LOCALIZACAO_USUARIO_LOGADO = "localizacaoUsuarioLogado";
    private static final String PERFIL_USUARIO_LOGADO = "perfilUsuarioLogado";
    private static final String DATA_ATUAL = "dataAtual";
    private static final String DATA_ATUAL_FORMATADA = "dataAtualFormatada";
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

    @Factory(LOCALIZACAO_USUARIO_LOGADO)
    public String getLocalizacaoUsuarioLogado() {
        return extractObjectStringValue(getLocalizacaoAtual());
    }

    @Factory(PERFIL_USUARIO_LOGADO)
    public String getPerfilUsuarioLogado() {
        return extractObjectStringValue(getUsuarioLocalizacaoAtual());
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
        return DateFormat.getDateInstance(style).format(
                gregorianCalendar.getTime());
    }

}
