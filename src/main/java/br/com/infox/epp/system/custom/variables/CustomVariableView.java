package br.com.infox.epp.system.custom.variables;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.seam.exception.BusinessException;

@ViewScoped
@Named
public class CustomVariableView implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final String TAB_FORM = "form";
	private static final NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
	
	@Inject
	private CustomVariableDao customVariableDao;
	@Inject 
	private CustomVariableSearch customVariableSearch;
	@Inject
	private CustomVariableDataList customVariableDataList;
	
	private String tab;
	private Long id;
	private String codigoVariavel;
	private TipoCustomVariableEnum tipoVariavel;
	private Boolean booleanValorVariavel;
	private Date dateValorVariavel;
	private String stringValorVariavel;
	
	public void select(CustomVariable customVariable) throws ParseException {
		this.id = customVariable.getId();
		setCodigoVariavel(customVariable.getCodigo());
		setTipoVariavel(customVariable.getTipo());
		setValorVariavel(customVariable);
		setTab(TAB_FORM);
	}
	
	@ExceptionHandled(MethodType.PERSIST)
	public void persist() {
		validaValorString();
		CustomVariable customVariable = new CustomVariable();
		aplicaValores(customVariable);
		customVariableDao.persist(customVariable);
		this.id = customVariable.getId();
	}

	@ExceptionHandled(MethodType.UPDATE)
	public void update() {
		validaValorString();
		CustomVariable customVariable = customVariableSearch.getById(id);
		aplicaValores(customVariable);
		customVariableDao.update(customVariable);
	}
	
	private void aplicaValores(CustomVariable customVariable) {
		customVariable.setCodigo(getCodigoVariavel());
		customVariable.setTipo(getTipoVariavel());
		customVariable.setValor(getValorVariavel());
	}
	
	@ExceptionHandled(MethodType.REMOVE)
	public void	remove(CustomVariable customVariable) {
		customVariableDao.remove(customVariable);
		customVariableDataList.refresh();
	}
	
	public void onClickSearchTab() {
		newInstance();
		customVariableDataList.refresh();
	}
	
	public void onClickFormTab() {
		setTab(TAB_FORM);
	}
	
	public void validaValorString() {
		try {
			switch (getTipoVariavel()) {
			case LONG:
				new Long(getStringValorVariavel());
				break;
			case DOUBLE:
				format.parse(getStringValorVariavel()).doubleValue();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw new BusinessException("Não foi possível converter o valor para o tipo " + getTipoVariavel().getLabel());
		}
	}
	
	public void newInstance() {
		this.id = null;
		this.codigoVariavel = null;
		this.tipoVariavel = null;
		this.booleanValorVariavel = null;
		this.dateValorVariavel = null;
		this.stringValorVariavel = null;
	}
	
	public boolean isManaged() {
		return id != null;
	}
	
	public boolean isTipoBoolean() {
		return TipoCustomVariableEnum.BOOLEAN.equals(getTipoVariavel());
	}
	
	public boolean isTipoDate() {
		return TipoCustomVariableEnum.DATE.equals(getTipoVariavel());
	}
	
	public boolean isTipoString() {
		return TipoCustomVariableEnum.STRING.equals(getTipoVariavel()) || TipoCustomVariableEnum.DOUBLE.equals(getTipoVariavel()) || 
				TipoCustomVariableEnum.LONG.equals(getTipoVariavel());
	}
	
	public TipoCustomVariableEnum[] getTiposDisponiveis() {
		return TipoCustomVariableEnum.values();
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getCodigoVariavel() {
		return codigoVariavel;
	}

	public void setCodigoVariavel(String codigoVariavel) {
		this.codigoVariavel = codigoVariavel;
	}

	public TipoCustomVariableEnum getTipoVariavel() {
		return tipoVariavel;
	}

	public void setTipoVariavel(TipoCustomVariableEnum tipoVariavel) {
		this.tipoVariavel = tipoVariavel;
	}

	private String getValorVariavel() {
		switch (getTipoVariavel()) {
		case BOOLEAN:
			return getBooleanValorVariavel().toString();
		case DATE:
			return new SimpleDateFormat(CustomVariable.DATE_PATTERN).format(getDateValorVariavel());
		default:
			return getStringValorVariavel();
		}
	}
	
	private void setValorVariavel(CustomVariable customVariable) throws ParseException {
		switch (customVariable.getTipo()) {
		case BOOLEAN:
			setBooleanValorVariavel(new Boolean(customVariable.getValor()));
			break;
		case DATE:
			setDateValorVariavel(new SimpleDateFormat(CustomVariable.DATE_PATTERN).parse(customVariable.getValor()));
			break;
		default:
			setStringValorVariavel(customVariable.getValor());
			break;
		}
	}

	public Boolean getBooleanValorVariavel() {
		return booleanValorVariavel;
	}

	public void setBooleanValorVariavel(Boolean booleanValorVariavel) {
		this.booleanValorVariavel = booleanValorVariavel;
	}

	public Date getDateValorVariavel() {
		return dateValorVariavel;
	}

	public void setDateValorVariavel(Date dateValorVariavel) {
		this.dateValorVariavel = dateValorVariavel;
	}

	public String getStringValorVariavel() {
		return stringValorVariavel;
	}

	public void setStringValorVariavel(String stringValorVariavel) {
		this.stringValorVariavel = stringValorVariavel;
	}
	
}
