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

import java.util.Date;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.entity.Parametro;
import br.com.infox.ibpm.entity.log.LogUtil;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

@Name(ParametroHome.NAME)
public class ParametroHome extends AbstractParametroHome<Parametro> {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ParametroHome.class);
	public static final String NAME = "parametroHome";
	public static final String ID_USUARIO_SISTEMA = "idUsuarioSistema";
	
	@Override
	public String persist() {		 
		String ret = null;
		try{
			ret = super.persist();
		}
		catch (Exception e) {
			LOG.error(".persit()", e);
		} 
		return ret;	
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setUsuarioModificacao(Authenticator.getUsuarioLogado());
		getInstance().setDataAtualizacao(new Date());
		return true;
	}
	
	public String getParametroOrFalse(String nome) {
		return ParametroUtil.getParametroOrFalse(nome);
	}	
	
	public String getIdPagina() {
		return LogUtil.getIdPagina();
	}
	
	@Override
	public String remove(Parametro obj) {
		obj.setAtivo(Boolean.FALSE);
		getInstance().setDataAtualizacao(new Date());
		return super.remove(obj);
	}

	public static UsuarioLogin getUsuarioSistema() {
		int idUsuarioSistema = Integer.parseInt(ParametroUtil.getParametro(ID_USUARIO_SISTEMA));
		return EntityUtil.getEntityManager().find(UsuarioLogin.class, idUsuarioSistema);
	}
}