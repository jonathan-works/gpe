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