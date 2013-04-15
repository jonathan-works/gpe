package br.com.infox.epa.service;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.ProcessoEpaDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.home.Authenticator;
import br.com.itx.util.Crypto;
import br.com.itx.util.EntityUtil;

@Name(ProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoManager extends GenericManager {
	
	private static final long serialVersionUID = 8095772422429350875L;
	public static final String NAME = "processoManager";
	
	@In private ProcessoEpaDAO processoEpaDAO;
	
	public ProcessoDocumentoBin createProcessoDocumentoBin(Object value, String certChain, String signature) {
		ProcessoDocumentoBin bin = new ProcessoDocumentoBin();
		bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
		bin.setDataInclusao(new Date());
		bin.setMd5Documento(Crypto.encodeMD5(String.valueOf(value)));
		bin.setUsuario(Authenticator.getUsuarioLogado());
		bin.setCertChain(certChain);
		bin.setSignature(signature);
		EntityUtil.getEntityManager().persist(bin);
		return bin;
	}
	
	public ProcessoDocumento createProcessoDocumento(Processo processo, String label, ProcessoDocumentoBin bin, TipoProcessoDocumento tipoProcessoDocumento) {
		ProcessoDocumento doc = new ProcessoDocumento();
		doc.setProcessoDocumentoBin(bin);
		doc.setAtivo(Boolean.TRUE);
		doc.setDataInclusao(new Date());
		doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
		doc.setProcesso(processo);
		if (label == null) {
			doc.setProcessoDocumento("null");
		} else {
			doc.setProcessoDocumento(label);
		}
		doc.setTipoProcessoDocumento(tipoProcessoDocumento);
		EntityUtil.getEntityManager().persist(doc);
		return doc;
	}
	
	private String getDescricaoModeloDocumentoByValue(Object value) {
		String modeloDocumento = String.valueOf(value);
		if (Strings.isEmpty(modeloDocumento)){
			modeloDocumento = " ";
		}
		return modeloDocumento;
	}
	
	/**
	 * Retorna, se houver, o novo valor do ModeloDocumento. Se nao houver, retorna o valor o valor
	 * inicial inalterado
	 * @param value - valor da variável modeloDocumento no contexto jBPM
	 * */
	public Object getAlteracaoModeloDocumento(ProcessoDocumentoBin processoDocumentoBinAtual, Object value) {
		if(processoDocumentoBinAtual.getModeloDocumento() != null) {
			value = processoDocumentoBinAtual.getModeloDocumento();
		}
		return value;
	}
	
	public void addProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().add(processoConexo);
	}
	
	public void removeProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().remove(processoConexo);
	}
	
	public void addProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo){
		processoAtual.getProcessoConexoListForIdProcessoConexo().add(processoConexo);
		update(processoAtual);
	}
	
	public void removeProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo){
		processoAtual.getProcessoConexoListForIdProcessoConexo().remove(processoConexo);
		update(processoAtual);
	}
	
	public boolean hasPartes(Processo processo){
		return processoEpaDAO.hasPartes(processo);
	}
}
