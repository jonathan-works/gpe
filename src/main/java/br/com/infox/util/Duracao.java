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
package br.com.infox.util;


public class Duracao {
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	public Duracao() {
		
	}
	
	public Duracao(long time) {
		int secDiv = 1000;
		int minDiv = secDiv * 60;
		int hourDiv = minDiv * 60;
		int dayDiv = hourDiv * 24;
		day = (int) (time / dayDiv);
		time = time % dayDiv;
		hour = (int) (time / hourDiv);
		time = time % hourDiv;
		minute = (int) (time / minDiv);
		time = time % minDiv;
		second = (int) (time / secDiv);
	}
	
	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return day + "d " + hour + "h " + minute + "m " + second + "s";
	}
}