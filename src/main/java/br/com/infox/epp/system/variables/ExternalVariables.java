package br.com.infox.epp.system.variables;

import static br.com.infox.epp.access.api.Authenticator.getLocalizacaoAtual;
import static br.com.infox.epp.access.api.Authenticator.getUsuarioLocalizacaoAtual;
import static br.com.infox.epp.access.api.Authenticator.getPapelAtual;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.home.ProcessoHome;

/**
 * @author erikliberal Classe criada para gerenciar variáveis que deverão ser
 *         utilizadas pelo usuário final, a exemplo em modelos de documento. Por
 *         motivos de segurança todos os valores resultantes devem ser tipos
 *         primitivos ou encapsulamento de valores primitivos
 */
@Name(ExternalVariables.NAME)
@Scope(ScopeType.EVENT)
public class ExternalVariables implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String NULL_STRING = "-";
    private static final int INT_ZERO_VALUE = 0;

    private static final String PARTES_PROCESSO_ATUAL = "partesProcessoAtual";
    private static final String PAPEL_USUARIO_LOGADO = "papelUsuarioLogado";
    private static final String NUMERO_PROCESSO_ATUAL = "numeroProcessoAtual";
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
    
    @Factory(PAPEL_USUARIO_LOGADO)
    public String getPapelUsuarioLogado() {
        return extractObjectStringValue(getPapelAtual());
    }

    @Factory(PERFIL_USUARIO_LOGADO)
    public String getPerfilUsuarioLogado() {
        return extractObjectStringValue(getUsuarioLocalizacaoAtual());
    }

    @Factory(NUMERO_PROCESSO_ATUAL)
    public String getNumeroProcessoAtual() {
        final Processo processo = ((ProcessoHome) Component
                .getInstance(ProcessoHome.NAME)).getInstance();
        String result;
        if (processo == null) {
            result = NULL_STRING;
        } else {
            result = processo.getNumeroProcesso();
        }
        return result;
    }
    
    @Factory(PARTES_PROCESSO_ATUAL)
    public String getPartesProcessoAtual() {
        final ProcessoEpa processo = (ProcessoEpa) ((ProcessoHome) Component
                .getInstance(ProcessoHome.NAME)).getInstance();
        String result;
        if (processo == null) {
            result = NULL_STRING;
        } else {
            result = extractObjectStringValue(processo.getPartes());
        }
        return result;
    }

    private String extractObjectStringValue(Object object) {
        String result;
        if (object == null) {
            result = NULL_STRING;
        } else {
            result = object.toString();
        }
        return result;
    }

    private String getCurrentDateFormattedValue(final int style) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(GregorianCalendar.MILLISECOND, INT_ZERO_VALUE);
        gregorianCalendar.set(GregorianCalendar.SECOND, INT_ZERO_VALUE);
        gregorianCalendar.set(GregorianCalendar.MINUTE, INT_ZERO_VALUE);
        gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, INT_ZERO_VALUE);
        return DateFormat.getDateInstance(style).format(
                gregorianCalendar.getTime());
    }

}
