package br.com.infox.epp.processo.documento.manager;

import java.util.List;

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
    
    public List<Pasta> getByProcesso(Processo processo) throws DAOException {
        List<Pasta> pastaList = getDao().getByProcesso(processo);
        if (pastaList == null || pastaList.isEmpty()) {
            createDefaultFolders(processo);
            pastaList = getDao().getByProcesso(processo);
        }
        return pastaList;
    }
    
    public void createDefaultFolders(Processo processo) throws DAOException {
        Pasta documentosProcesso = new Pasta();
        documentosProcesso.setRemovivel(Boolean.FALSE);
        documentosProcesso.setVisivelExterno(Boolean.TRUE);
        documentosProcesso.setNome("Documentos do Processo");
        documentosProcesso.setProcesso(processo);
        
        Pasta naoAceitos = new Pasta();
        naoAceitos.setRemovivel(Boolean.FALSE);
        naoAceitos.setVisivelExterno(Boolean.FALSE);
        naoAceitos.setNome("Não Aceitos");
        naoAceitos.setProcesso(processo);
        
        persist(documentosProcesso);
        persist(naoAceitos);
        
        documentoService.setDefaultFolder(documentosProcesso);
    }
}
