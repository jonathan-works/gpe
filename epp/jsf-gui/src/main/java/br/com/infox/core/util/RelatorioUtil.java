package br.com.infox.core.util;

import java.util.Date;

import br.com.infox.epp.access.api.Authenticator;

public class RelatorioUtil {

    private static final String EMITIDO_POR = "Emitido por:";
    private static final String EMITIDO_EM  = "Emitido em:";

    public static String getDadosEmissao() {
        String usuario = Authenticator.getUsuarioLogado().getNomeUsuario();
        String dataHora = DateUtil.formatarData(new Date(), "dd/MM/yyyy HH:mm");
        return EMITIDO_POR + " " + usuario + ". " + EMITIDO_EM + " " + dataHora;
    }

}
