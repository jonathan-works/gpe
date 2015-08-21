package br.com.infox.epp.processo.documento.manager;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.manager.ModeloPastaManager;
import br.com.infox.epp.processo.documento.dao.PastaDAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.filter.DocumentoFilter;
import br.com.infox.epp.processo.documento.service.DocumentoService;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;

@AutoCreate
@Name(PastaManager.NAME)
public class PastaManager extends Manager<PastaDAO, Pasta> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaManager";
    
    @In
    private DocumentoService documentoService;
    @In
    private MetadadoProcessoManager metadadoProcessoManager;
    @In
    private PastaRestricaoManager pastaRestricaoManager;
    @In
    private ModeloPastaManager modeloPastaManager;
    @In
    private ProcessoManager processoManager;
    
    public Pasta getDefaultFolder(Processo processo) throws DAOException {
        Pasta pasta = getDefault(processo);
        return pasta;
    }
    
    public List<Pasta> getByProcesso(Processo processo) throws DAOException {
        List<Pasta> pastaList = getDao().getByProcesso(processo);
        return pastaList;
    }
    
    public int getTotalDocumentosPasta(Pasta pasta) {
    	return getDao().getTotalDocumentosPasta(pasta);
    }
    
    public int getTotalDocumentosPastaPorFiltros(Pasta pasta, DocumentoFilter documentoFilter) {
    	return getDao().getTotalDocumentosPastaPorFiltros(pasta, documentoFilter);
    }
    public int getTotalDocumentosPasta(Pasta pasta, String customFilter, Map<String, Object> params) {
    	return getDao().getTotalDocumentosPasta(pasta, customFilter, params);
    }
    
    public String getNomePasta(Pasta pasta) {
    	return getNomePasta(pasta, getTotalDocumentosPasta(pasta));
    }
    
    public String getNomePasta(Pasta pasta, int totalDocumentos) {
    	return MessageFormat.format(pasta.getTemplateNomePasta(), totalDocumentos);
    }
    
    public String getNomePasta(Pasta pasta,  DocumentoFilter documentoFilter) {
    	return getNomePasta(pasta, getTotalDocumentosPastaPorFiltros(pasta, documentoFilter));
    }
    
    public List<Pasta> createDefaultFolders(Processo processo) throws DAOException {
        Processo root = processo.getProcessoRoot();
        List<Pasta> pastaList = root.getPastaList();
        List<ModeloPasta> modeloPastaList = modeloPastaManager.getByFluxo(processo.getNaturezaCategoriaFluxo().getFluxo());
        for (ModeloPasta modeloPasta : modeloPastaList) {
            pastaList.add(createFromModelo(modeloPasta, processo));
        }
        Pasta padrao = getDefault(processo.getProcessoRoot());
        if (padrao == null && !pastaList.isEmpty()) {
            padrao = pastaList.get(0);
            documentoService.setDefaultFolder(padrao);
            metadadoProcessoManager.addMetadadoProcesso(processo, EppMetadadoProvider.PASTA_DEFAULT, padrao.getId().toString());
        }
        return pastaList;
    }
    
    private Pasta createFromModelo(ModeloPasta modeloPasta, Processo processo) throws DAOException {
        Pasta pasta = new Pasta();
        pasta.setNome(modeloPasta.getNome());
        pasta.setRemovivel(modeloPasta.getRemovivel());
        pasta.setProcesso(processo.getProcessoRoot());
        pasta.setSistema(modeloPasta.getSistema());
        pasta.setEditavel(modeloPasta.getEditavel());
        pasta.setDescricao(modeloPasta.getDescricao());
        pasta.setOrdem(modeloPasta.getOrdem());
        
        persist(pasta);
        pastaRestricaoManager.createRestricoesFromModelo(modeloPasta, pasta);
        return pasta;
    }

    public Pasta getDefault(Processo processo) {
    	List<MetadadoProcesso> metaPastas = metadadoProcessoManager.getMetadadoProcessoByType(processo, EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType());
        if (!metaPastas.isEmpty()) {
        	return (Pasta)metaPastas.get(0).getValue();
        } else {
        	return null;
        }
    }
    
    public Pasta persistWithDefault(Pasta o) throws DAOException {
    	Boolean editavel = (o.getEditavel() == null) ? Boolean.TRUE : o.getEditavel();
    	o.setEditavel(editavel);
		Boolean removivel = (o.getRemovivel() == null) ? Boolean.TRUE : o.getRemovivel();
		o.setRemovivel(removivel);
    	Pasta pasta = super.persist(o);
    	pastaRestricaoManager.persistRestricaoDefault(pasta); 
    	return pasta;
    }

    public void deleteComRestricoes(Pasta pasta) throws DAOException {
        pastaRestricaoManager.deleteByPasta(pasta);
        remove(pasta);
    }
    
    public Pasta getByProcessoAndDescricao(Processo processo, String descricao) {
        return getDao().getByProcessoAndDescricao(processo, descricao);
    }
    
    public Pasta getPastaByNome(String nome, Processo processo) {
    	return getDao().getPastaByNome(nome, processo);
    }
    
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso) throws DAOException {
    	Processo processo = processoManager.find(idProcesso.intValue()); 
    	if (processo != null) {
    		Pasta pasta = getDao().getByProcessoAndDescricao(processo.getProcessoRoot(), descricaoPasta);
    		if (pasta != null) {
    			PastaRestricao pastaRestricao = pastaRestricaoManager.getByPastaAlvoTipoRestricao(pasta, 1, PastaRestricaoEnum.R);
    			if (pastaRestricao == null) {
    				pastaRestricao = new PastaRestricao();
    				pastaRestricao.setAlvo(1);
    				pastaRestricao.setTipoPastaRestricao(PastaRestricaoEnum.R);
    				pastaRestricao.setWrite(false);
    				pastaRestricao.setDelete(false);
    				pastaRestricao.setRead(true);
    				pastaRestricao.setPasta(pasta);
    				pastaRestricaoManager.persist(pastaRestricao);
    			} else {
    				pastaRestricao.setRead(true);
    				pastaRestricaoManager.update(pastaRestricao);
    			}
    		}
    	}
    }
    
    
}