package br.com.infox.epp.access.crud;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.util.ComponentUtil;

@Scope(ScopeType.CONVERSATION)
@Name(value=TermoAdesaoAction.NAME)
public class TermoAdesaoAction implements Serializable {
    private static final String PARAMETRO_TERMO_ADESAO = "termoAdesao";
    private static final long serialVersionUID = 1L;
    public static final String NAME = "termoAdesaoAction";
    public static final String PANEL_NAME = "termoAdesaoPanel";
    
    @In
    private ParametroManager parametroManager;
    @In
    private ModeloDocumentoManager modeloDocumentoManager;

    public String getTermoAdesao() {
        String result = null;
        Parametro parametro = parametroManager.getParametro(PARAMETRO_TERMO_ADESAO);
        if (parametro != null) {
            ModeloDocumento modeloDocumento = modeloDocumentoManager.getModeloDocumentoByTitulo(parametro.getValorVariavel());
            result = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);
        }
        if (result == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<div><div>");
            sb.append("Cu vix saepe iudico, posse bonorum qualisque ut pri. Id eos clita legere mandamus, cibo insolens phaedrum qui id. Duis affert luptatum ei ius. Reque impedit expetenda vix et, has prodesset rationibus ea, an gubergren disputando intellegebat vim. Ornatus appareat an nec.");
            sb.append("</div><div></div><div>");
            sb.append("Ex vix aperiri feugiat maluisset, erat eripuit no eos, sed ullum decore vituperatoribus an. Noluisse reprimique his eu, vim officiis antiopam inciderint ut. Ferri labitur fabulas te vix. Pro ea everti reformidans. Has eius doming te, invenire constituam eum an. In his nemore feugiat, at cum discere aliquando, veritus nominavi ne eum. Scripta suscipiantur ei mea, ne nominati dignissim mea.");
            sb.append("</div><div></div><div>");
            sb.append("Dicta iudico an pri, erant quidam oblique eum in. Erant iuvaret cu mei. Nam cu offendit copiosae, an dicta exerci graece mel. Duo everti reprimique cu. Solum erant id pro.");
            sb.append("</div><div></div><div>");
            sb.append("Id modo efficiendi reprehendunt eam. Vel at iuvaret convenire intellegebat, autem ancillae ut nec, malis nulla commune qui in. Qui porro efficiantur cu. Vix idque theophrastus ad, noluisse abhorreant quo cu. Ut posse aeque eloquentiam vel.");
            sb.append("</div><div></div><div>");
            sb.append("Vix purto elit elitr at, no diceret assueverit sadipscing his, duo regione ancillae consequat ne. Eu usu soleat conceptam. Ea vis prima congue maiestatis, vis id malis pertinacia intellegebat. Mei propriae principes te. Quis paulo inimicus nam eu.");
            sb.append("</div></div>");
            result = sb.toString();
        }
        return result;
    }
    
    public void setTermoAdesao(String termoAdesao) {
        
    }
    
    public String getTermoAdesaoPanelName() {
        return PANEL_NAME;
    }
    public void setTermoAdesaoPanelName(String name) {
    }
}
