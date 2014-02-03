/*
 * IBPM - Ferramenta de produtividade Java Copyright (c) 1986-2009 Infox
 * Tecnologia da Informação Ltda.
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob
 * os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela Free
 * Software Foundation; versão 2 da Licença. Este programa é distribuído na
 * expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 * ESPECÍFICA.
 * 
 * Consulte a GNU GPL para mais detalhes. Você deve ter recebido uma cópia da
 * GNU GPL junto com este programa; se não, veja em http://www.gnu.org/licenses/
 */

package br.com.infox.epp.documento.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.epp.documento.entity.DocumentoBin;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(DocumentoBinHome.NAME)
public class DocumentoBinHome extends AbstractHome<DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinHome";
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoBinHome.class);

    @Override
    protected String getPersistenceContextName() {
        return "entityManagerBin";
    }

    /**
     * Grava o arquivo binário na base de arquivos Bin com o respctivo Id da
     * tabela ProcessoDocumentoBin.
     * 
     * @param idDocumentoBin Id da Tabela
     * @param file Arquivo do tipo byte[]
     * @return True caso não aconteça nenhum erro.
     */
    public boolean setData(int idDocumentoBin, byte[] file) {
        DocumentoBin instance = getInstance();
        instance.setIdDocumentoBin(idDocumentoBin);
        instance.setDocumentoBin(file);
        boolean ret = persistData();
        newInstance();
        return ret;
    }

    public boolean persistData() {
        try {
            getEntityManager().persist(getInstance());
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao persistir o arquivo.");
            LOG.error(".persistData()", e);
            return false;
        }
    }

    public static DocumentoBinHome instance() {
        return ComponentUtil.getComponent(NAME);
    }

}
