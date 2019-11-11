package br.com.infox.epp.processo.status;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.status.entity.StatusProcesso;

@Named
@ViewScoped
public class StatusProcessoList extends DataList<StatusProcesso> {

private static final long serialVersionUID = 1L;
    
private static final String DEFAULT_JPQL = "select o from StatusProcesso o ";
private static final String DEFAULT_ORDER = "o.descricao";

private String nome;
private String descricao;
private Boolean ativo;

@Override
protected String getDefaultOrder() {
    return DEFAULT_ORDER;
}

@Override
protected String getDefaultEjbql() {
    return DEFAULT_JPQL;
}

@Override
protected void addRestrictionFields() {
    addRestrictionField("nome", RestrictionType.contendo);
    addRestrictionField("descricao", RestrictionType.contendo);
    addRestrictionField("ativo", RestrictionType.igual);
}

@Override
protected Map<String, String> getCustomColumnsOrder() {
    Map<String, String> orderMap = new HashMap<>(3);
    orderMap.put("nome", "o.nome");
    orderMap.put("descricao", "o.descricao");
    orderMap.put("ativo", "o.ativo");
    return orderMap;
}

public String getNome() {
    return nome;
}

public void setNome(String nome) {
    this.nome = nome;
}

public String getDescricao() {
    return descricao;
}

public void setDescricao(String descricao) {
    this.descricao = descricao;
}

public Boolean getAtivo() {
	return ativo;
}

public void setAtivo(Boolean ativo) {
	this.ativo = ativo;
}

}
