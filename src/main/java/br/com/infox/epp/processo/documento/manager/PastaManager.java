package br.com.infox.epp.processo.documento.manager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.dao.PastaDAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.service.DocumentoService;
import br.com.infox.epp.processo.entity.Processo;

@AutoCreate
@Name(PastaManager.NAME)
public class PastaManager extends Manager<PastaDAO, Pasta> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaManager";
    
    @In
    private DocumentoService documentoService;
    
    public Pasta getDefaultFolder(Processo processo) throws DAOException {
        Pasta pasta = getDao().getDefaultByProcesso(processo);
        if (pasta == null) {
        	pasta = createDefaultFolders(processo).get(0); 
        }
        return pasta;
    }
    
    public List<Pasta> getByProcesso(Processo processo) throws DAOException {
        List<Pasta> pastaList = getDao().getByProcesso(processo);
        if (pastaList == null || pastaList.isEmpty()) {
            pastaList = createDefaultFolders(processo); 
        }
        return pastaList;
    }
    
    public int getTotalDocumentosPasta(Pasta pasta) {
    	return getDao().getTotalDocumentosPasta(pasta);
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
    
    private List<Pasta> createDefaultFolders(Processo processo) throws DAOException {
        Pasta documentosProcesso = new Pasta();
        documentosProcesso.setRemovivel(Boolean.FALSE);
        documentosProcesso.setVisivelExterno(Boolean.TRUE);
        documentosProcesso.setVisivelNaoParticipante(Boolean.TRUE);
        documentosProcesso.setNome("Documentos do Processo");
        documentosProcesso.setProcesso(processo);
        documentosProcesso.setSistema(Boolean.TRUE);
        
        Pasta naoAceitos = new Pasta();
        naoAceitos.setRemovivel(Boolean.FALSE);
        naoAceitos.setVisivelExterno(Boolean.FALSE);
        naoAceitos.setVisivelNaoParticipante(Boolean.FALSE);
        naoAceitos.setNome("Não Aceitos");
        naoAceitos.setProcesso(processo);
        naoAceitos.setSistema(Boolean.TRUE);
        
        persist(documentosProcesso);
        persist(naoAceitos);
        
        documentoService.setDefaultFolder(documentosProcesso);
        List<Pasta> pastas = new ArrayList<>();
        pastas.add(documentosProcesso);
        pastas.add(naoAceitos);
        return pastas;
    }
}
