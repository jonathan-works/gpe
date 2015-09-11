package br.com.infox.epp.view.municipio;

public class MunicipioBean {
    
	private String ccod;
    private String tnom;
    
    public MunicipioBean() {
	}
    
	public MunicipioBean(String ccod) {
		this.ccod = ccod;
	}

	public MunicipioBean(String ccod, String tnom) {
        this.ccod = ccod;
        this.tnom = tnom;
    }

    public String getCcod() {
        return ccod;
    }

    public void setCcod(String ccod) {
        this.ccod = ccod;
    }

    public String getTnom() {
        return tnom;
    }

    public void setTnom(String tnom) {
        this.tnom = tnom;
    }
    
    @Override
    public String toString() {
        return tnom;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ccod == null) ? 0 : ccod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MunicipioBean))
			return false;
		MunicipioBean other = (MunicipioBean) obj;
		if (ccod == null) {
			if (other.ccod != null)
				return false;
		} else if (!ccod.equals(other.ccod))
			return false;
		return true;
	}
    
}
