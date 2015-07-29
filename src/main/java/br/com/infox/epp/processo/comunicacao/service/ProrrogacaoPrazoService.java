package br.com.infox.epp.processo.comunicacao.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.seam.exception.BusinessException;

@Name(ProrrogacaoPrazoService.NAME)
@AutoCreate
@Stateless
@Scope(ScopeType.STATELESS)
@Transactional
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProrrogacaoPrazoService {
	public static final String NAME = "prorrogacaoPrazoService";
	
	public Boolean isClassificacaoProrrogacaoPrazo(ClassificacaoDocumento classificacaoDocumento, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canShowClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		return false;
	}
	
	public Boolean containsClassificacaoProrrogacaoPrazo(List<Documento> documentos, TipoComunicacao tipoComunicacao) {
		return false;
	}
	
	public Boolean canRequestProrrogacaoPrazo(TipoComunicacao tipoComunicacao){
		return false;
	}
	
	public ClassificacaoDocumento getClassificacaoProrrogacaoPrazo(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		throw new BusinessException("O tipo de comunicação " + destinatarioModeloComunicacao.getModeloComunicacao().getTipoComunicacao().getDescricao() + " não admite pedido de prorrogação de prazo");
	}
	
	public void finalizarAnalisePedido(Processo comunicacao) throws DAOException{
		TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
	}
	
	public Date getDataPedidoProrrogacao(Processo comunicacao){
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_PEDIDO_PRORROGACAO);
    	if(metadadoProcesso != null) {
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }
    
    public boolean hasPedidoProrrogacaoEmAberto(Processo comunicacao){
    	return getDataPedidoProrrogacao(comunicacao) != null && getDataAnaliseProrrogacao(comunicacao) == null;
    }
    
    public Date getDataAnaliseProrrogacao(Processo comunicacao){
    	MetadadoProcesso metadadoProcesso = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_ANALISE_PRORROGACAO);
    	if(metadadoProcesso != null) {
    		return metadadoProcesso.getValue();
    	}
    	return null;
    }
    
    public boolean isPrazoProrrogado(Processo comunicacao){
    	return  !getDataLimiteCumprimentoInicial(comunicacao).equals(
    					comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO).getValue());
    }
    
    public Date getDataLimiteCumprimentoInicial(Processo comunicacao){
    	return comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO_INICIAL).getValue();
    }
    
    public String getStatusProrrogacaoFormatado(Processo comunicacao){
    	if(comunicacao != null && getDataPedidoProrrogacao(comunicacao) != null){
    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    		if (hasPedidoProrrogacaoEmAberto(comunicacao)){
    			return "Aguardando análise desde " + dateFormat.format(getDataPedidoProrrogacao(comunicacao));
    		}else{
    			String dataAnalise = dateFormat.format(getDataAnaliseProrrogacao(comunicacao)); 
				if(isPrazoProrrogado(comunicacao)){
					return "Prazo original: " + dateFormat.format(getDataLimiteCumprimentoInicial(comunicacao));
				}else{
					return "Prorrogação negada em " + dataAnalise;
				}
    		}
    	}
		return "";
    }
	
}
