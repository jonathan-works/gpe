package br.com.infox.ibpm.home;

import br.com.infox.ibpm.entity.Status;
import br.com.itx.component.AbstractHome;

public abstract class AbstractStatusHome<T> extends AbstractHome<Status> {

	private static final long serialVersionUID = 1L;

	public void setStatusIdStatus(Integer id) {
		setId(id);
	}

	public Integer getStatusIdStatus() {
		return (Integer) getId();
	}
	
	public String remove(Status obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("statusGrid");
		return ret;
	}
}