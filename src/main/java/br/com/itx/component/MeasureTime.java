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
package br.com.itx.component;

import org.jboss.util.StopWatch;

public class MeasureTime {
	private StopWatch watch;

	public MeasureTime() {
		watch = new StopWatch();
	}
	
	public MeasureTime(boolean start) {
		this();
		if (start) {
			start();
		}
	}	
	
	public MeasureTime start() {
		watch.start();
		return this;
	}
	
	public long stop() {
		return watch.stop();
	}
	
	public long getTime() {
		return watch.getTime();
	}
	
	public void print(String msg) {
		System.out.println(msg + ". Tempo decorrido: " + watch.getTime());
	}
	
	public void reset() {
		watch.reset();
	}
	
	public void resetAndStart() {
		reset();
		start();
	}	

}