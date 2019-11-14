package br.com.infox.util.time;

import java.io.Serializable;
import java.util.Date;

public class Periodo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date from;
	private Date to;

	public Periodo() {
		setFrom(null);
		setTo(null);
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
