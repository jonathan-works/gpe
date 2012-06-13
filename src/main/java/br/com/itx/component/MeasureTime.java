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