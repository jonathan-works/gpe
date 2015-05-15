package br.com.infox.epp.processo.comunicacao.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.seam.exception.BusinessException;

@Name(ProrrogacaoPrazoService.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
@Transactional
public class ProrrogacaoPrazoService {
	public static final String NAME = "prorrogacaoPrazoService";
	
	public boolean isClassificacaoProrrogacaoPrazo(ClassificacaoDocumento classificacaoDocumento, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public boolean canShowClassificacaoProrrogacaoPrazo(ModeloComunicacao modeloComunicacao) {
		return false;
	}
	
	public ClassificacaoDocumento getClassificacaoProrrogacaoPrazo(ModeloComunicacao modeloComunicacao) {
		throw new BusinessException("O tipo de comunicação " + modeloComunicacao.getTipoComunicacao().getDescricao() + " não admite pedido de prorrogação de prazo");
	}
}
