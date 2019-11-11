package br.com.infox.epp.assinador.rest;

public class ErroProcessamento {
	private String code;
	private String messsage;
	
	public ErroProcessamento(String code, String messsage) {
		this.code = code;
		this.messsage = messsage;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMesssage() {
		return messsage;
	}
	public void setMesssage(String messsage) {
		this.messsage = messsage;
	}
}
