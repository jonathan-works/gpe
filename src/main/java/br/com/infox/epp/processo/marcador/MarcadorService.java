package br.com.infox.epp.processo.marcador;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.util.ArrayUtil.ListConversor;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.seam.security.SecurityUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MarcadorService {
    
    @Inject
    protected SecurityUtil securityUtil;
    @Inject
    protected MarcadorSearch marcadorSearch;
    @Inject @GenericDao
    protected Dao<Marcador, Long> marcadorDao;
    
    public boolean isPermittedAdicionarMarcador() {
        return securityUtil.isPermitted("MarcadorDocumento/adicionar");
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createMarcadores(DocumentoBin documentoBin, List<String> codigoMarcadores, Processo processo) {
        if (codigoMarcadores == null || codigoMarcadores.isEmpty()) return;
        List<Marcador> listMarcadores = marcadorSearch.listMarcadorByProcessoAndInCodigosMarcadores(processo.getIdProcesso(), codigoMarcadores);
        for (String codigoMarcador : codigoMarcadores) {
            Marcador marcadorTemp = new Marcador(codigoMarcador);
            int index = -1;
            if ((index = listMarcadores.indexOf(marcadorTemp)) != -1) {
                marcadorTemp = listMarcadores.get(index);
            } else {
                marcadorDao.persist(marcadorTemp);
            }
            documentoBin.getMarcadores().add(marcadorTemp);
        }
    }
    
    public static ListConversor<Marcador, String> CONVERT_MARCADOR_CODIGO = new ListConversor<Marcador, String>() {
        @Override
        public String convert(Marcador T) {
            return T.getCodigo();
        }
    };

}
