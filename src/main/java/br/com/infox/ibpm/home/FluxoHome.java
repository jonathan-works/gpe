/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Identity;

import br.com.infox.ibpm.entity.Fluxo;

 
@Name(FluxoHome.NAME)
@BypassInterceptors
public class FluxoHome 
		extends AbstractFluxoHome<Fluxo>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "fluxoHome";
	private List<Fluxo> fluxoList;
	
	public static FluxoHome instance() {
		return (FluxoHome) Component.getInstance(NAME);
	}
	
	@Override
	public String persist() {
		String ret = null;
		try{
			ret = super.persist();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		return ret;	
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
		if (getInstance().getDataFimPublicacao() != null){
			if (getInstance().getDataFimPublicacao().before(getInstance().getDataInicioPublicacao())){
				FacesMessages.instance().add(Severity.ERROR, "Data Fim Publicação deve ser maior que a Data Iníco Publicação");
				return Boolean.FALSE;
			}
		}
		
		verificaPublicacao();
		return super.beforePersistOrUpdate();
	}
	
	

	@Override
	public String update() {
		String ret = null;
		try {
			ret = super.update();
		} catch (Exception e) {
			System.out.println("Erro de restrição: possivelmente um campo foi duplicado.");
		}
		return ret;
	}	

	
	/*Remoção Lógica. Se possuir algum processo vinculado, verifica se o msm 
	 * está arquivado ou não.
	 */
	@Override
	public String remove(Fluxo obj) {
		setInstance(obj);
		//Verifica se esta vinculado a algum processo
		String query = "select count(o) from Processo o " +
						"where o.fluxo = :fluxo2 ";
		Query q = getEntityManager().createQuery(query).setMaxResults(1);
		q.setParameter("fluxo2", obj);
		
		if ( ((Long)q.getSingleResult() <= 0)){
			obj.setAtivo(Boolean.FALSE);
			super.update();
			newInstance();
			refreshGrid("fluxoGrid");
		}
		else {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, 
					"Este registro está em uso e não poderá ser excluído.");
		}
		return "updated";
	}
	
	@SuppressWarnings("unchecked")
	public List<Fluxo> getFluxoList() { 
		if(fluxoList == null) {
			fluxoList = new ArrayList<Fluxo>();
			for (Fluxo f : (List<Fluxo>) getEntityManager()
					.createQuery("select f from Fluxo f where f.ativo = true order by f.fluxo")
					.getResultList()) {
				if(Identity.instance().hasRole("/fluxo/"+f.getFluxo())) {
					fluxoList.add(f);
				}
			}
		}
		return fluxoList;
	}
	
}