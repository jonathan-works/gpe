/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.manager.FluxoManager;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.IllegalXPDLException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.HibernateUtil;

 
@Name(FluxoHome.NAME)
public class FluxoHome 
		extends AbstractFluxoHome<Fluxo>{
    private static final LogProvider LOG = Logging.getLogProvider(FluxoHome.class);
	private static final long serialVersionUID = 1L;
	public static final String NAME = "fluxoHome";
	
	@In private FluxoManager fluxoManager;
	
	public static FluxoHome instance() {
		return ComponentUtil.getComponent(FluxoHome.NAME);
	}
	
	@Override
	public String persist() {
		String ret = null;
		try{
			ret = super.persist();
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		} 
		return ret;	
	}

	public String persistAs() {
		Fluxo fluxo = getInstance();
		HibernateUtil.getSession().evict(fluxo);
		fluxo.setIdFluxo(null);
		return persist();
	}
	
	private void verificaPublicacao(){
		Date data = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		String dataHoje = formato.format(data);
		String dataInicio = formato.format(getInstance().getDataInicioPublicacao());
		
		if (dataHoje.equals(dataInicio)){
			getInstance().setPublicado(Boolean.TRUE);
		}
	}

	@Override
	protected boolean beforePersistOrUpdate() {
	    Date dataFimPublicacao = getInstance().getDataFimPublicacao();
		if (dataFimPublicacao != null){
			if (dataFimPublicacao.before(getInstance().getDataInicioPublicacao())){
				FacesMessages.instance().add(Severity.ERROR, "Data Fim Publica��o deve ser maior que a Data In�co Publica��o");
				return Boolean.FALSE;
			}
		}
		
		verificaPublicacao();
		return true;
	}

	@Override
	public String update() {
		String ret = "";
		try {
			ret = super.update();
		} catch (Exception e) {
		    LOG.error("Erro de restri��o: possivelmente um campo foi duplicado.");
		}
		return ret;
	}	
	
	/*Remo��o L�gica. Se possuir algum processo vinculado, verifica se o msm 
	 * est� arquivado ou n�o.
	 */
    @Override
    public String remove(Fluxo obj) {
        setInstance(obj);
        String query = "select count(o) from Processo o "
                + "where o.fluxo = :fluxo2 ";
        Query q = getEntityManager().createQuery(query).setMaxResults(1);
        q.setParameter("fluxo2", obj);

        if (((Long) q.getSingleResult() <= 0)) {
            obj.setAtivo(Boolean.FALSE);
            super.update();
            newInstance();
        } else {
            final String message = "Este registro est� em uso e n�o poder� ser exclu�do.";
            LOG.warn(message);
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, message);
        }
        return "updated";
    }

    public String importarXPDL(byte[] bytes) {
    	Fluxo fluxo = getInstance();
		try {
			FluxoXPDL fluxoXPDL = FluxoXPDL.createInstance(bytes);
			fluxo.setXml(fluxoXPDL.toJPDL(fluxo.getFluxo()));
		} catch (IllegalXPDLException e) {
			LOG.error("Erro ao importar arquivo XPDL. " + e.getMessage());
		}
		StringBuilder result = new StringBuilder();
		result.append(FluxoHome.NAME).append(".update()");
		return result.toString();
	}
    
}