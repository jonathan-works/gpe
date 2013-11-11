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
package br.com.infox.epp.ajuda.component;

import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.component.AbstractImageFileUploader;
import br.com.itx.util.ComponentUtil;

@Name(HelpFileUploader.NAME)
public class HelpFileUploader extends AbstractImageFileUploader {
	public static final String NAME = "helpFileUploader";

	public static final HelpFileUploader instance() {
		return ComponentUtil.getComponent(NAME);
	}

    public static final String HELP_IMAGE_RELATIVE_PATH = "/img/help/";
	
	@Override
	public String getImagesRelativePath() {
		return HelpFileUploader.HELP_IMAGE_RELATIVE_PATH;
	}
	
}