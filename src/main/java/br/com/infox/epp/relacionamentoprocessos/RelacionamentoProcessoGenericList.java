package br.com.infox.epp.relacionamentoprocessos;

import javax.inject.Inject;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;

public abstract class RelacionamentoProcessoGenericList<T extends RelacionamentoProcesso> extends DataList<T> {
	private static final long serialVersionUID = 1L;

	private Class<T> classeRelacionamento;

	private Processo processo;
	private boolean readOnly;

	@Inject
	private RelacionamentoManager relacionamentoManager;

	protected RelacionamentoProcessoGenericList() {
		this.classeRelacionamento = getClasseRelacionamento();
	}

	protected abstract Class<T> getClasseRelacionamento();

	@Override
	protected String getDefaultEjbql() {
		return "select r2 from " + RelacionamentoProcessoInterno.class.getSimpleName() + " r1"
				+ " inner join r1.relacionamento r"
				+ " inner join r.relacionamentosProcessos r2";
	}

	@Override
	protected String getDefaultWhere() {
		String where = "where ("
				+ "\nr1.processo.idProcesso = " + this.processo.getIdProcesso()
				+ "\nand TYPE(r1) = " + RelacionamentoProcessoInterno.class.getSimpleName()
				+ "\nand TYPE(r2) = " + classeRelacionamento.getSimpleName();
		
				if(classeRelacionamento.equals(RelacionamentoProcessoInterno.class) || classeRelacionamento.equals(RelacionamentoProcesso.class)) {
					where += "\nand r2.processo.idProcesso <> " +  + getProcesso().getIdProcesso();
				}
				
				where += "\n)";
				
				return where;
	}

	@Override
	protected String getDefaultOrder() {
		return "r.dataRelacionamento desc";
	}

	@ExceptionHandled(MethodType.REMOVE)
	public void remove(RelacionamentoProcesso relacionamentoProcesso) {
		relacionamentoManager.remove(relacionamentoProcesso.getRelacionamento().getIdRelacionamento());
		refresh();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
}
