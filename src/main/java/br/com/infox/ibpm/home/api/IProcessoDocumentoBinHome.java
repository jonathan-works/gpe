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

package br.com.infox.ibpm.home.api;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.ProcessoDocumento;

public interface IProcessoDocumentoBinHome {

	String persist();
	
	String update();

	ProcessoDocumentoBin getInstance();

	void isModelo(boolean isModelo);

	void newInstance();
	
	ProcessoDocumento getProcessoDocumento();
}