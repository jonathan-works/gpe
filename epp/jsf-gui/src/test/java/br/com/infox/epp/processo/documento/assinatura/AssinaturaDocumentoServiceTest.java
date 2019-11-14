/**
 * 
 */
package br.com.infox.epp.processo.documento.assinatura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.com.infox.epp.documento.type.TipoAssinaturaEnum;

/**
 * @author erikliberal
 *
 */
public class AssinaturaDocumentoServiceTest {
    
    @Test
    public void isDocumentoTotalmenteAssinadoSemSuficienteSemObrigatorio() {
        AssinaturaDocumentoService service = new AssinaturaDocumentoService();
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = initMapAssinaturas();
        
        List<Boolean> facultativas = mapAssinaturas.get(TipoAssinaturaEnum.F);
        facultativas.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        facultativas.add(Boolean.FALSE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        List<Boolean> naoAssina = mapAssinaturas.get(TipoAssinaturaEnum.P);
        naoAssina.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        naoAssina.add(Boolean.FALSE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
    }

    private Map<TipoAssinaturaEnum, List<Boolean>> initMapAssinaturas() {
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = new HashMap<>();
        for (TipoAssinaturaEnum tipoAssinatura : TipoAssinaturaEnum.values()) {
            mapAssinaturas.put(tipoAssinatura, new ArrayList<Boolean>());
        }
        return mapAssinaturas;
    }

    @Test
    public void isDocumentoTotalmenteAssinadoSemObrigatorias() {
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = initMapAssinaturas();
        List<Boolean> suficientes = mapAssinaturas.get(TipoAssinaturaEnum.S);
        
        AssinaturaDocumentoService service = new AssinaturaDocumentoService();
        
        suficientes.add(Boolean.FALSE);
        Assert.assertFalse(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        suficientes.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
    }

    @Test
    public void isDocumentoTotalmenteAssinadoSemSuficientes() {
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = initMapAssinaturas();
        List<Boolean> obrigatorias = mapAssinaturas.get(TipoAssinaturaEnum.O);
        
        AssinaturaDocumentoService service = new AssinaturaDocumentoService();

        obrigatorias.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        obrigatorias.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        obrigatorias.add(Boolean.FALSE);
        Assert.assertFalse(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
    }
    
    @Test
    public void isDocumentoTotalmenteAssinado() {
        Map<TipoAssinaturaEnum, List<Boolean>> mapAssinaturas = initMapAssinaturas();
        List<Boolean> obrigatorias = mapAssinaturas.get(TipoAssinaturaEnum.O);
        List<Boolean> suficientes = mapAssinaturas.get(TipoAssinaturaEnum.S);
        
        AssinaturaDocumentoService service = new AssinaturaDocumentoService();

        obrigatorias.add(Boolean.FALSE);
        Assert.assertFalse(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        
        suficientes.add(Boolean.FALSE);
        Assert.assertFalse(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        
        obrigatorias.clear();
        obrigatorias.add(Boolean.TRUE);
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
        
        suficientes.clear();
        suficientes.add(Boolean.TRUE);
        
        obrigatorias.add(Boolean.FALSE);
        obrigatorias.add(Boolean.FALSE);
        obrigatorias.add(Boolean.FALSE);
        obrigatorias.add(Boolean.FALSE);
        
        Assert.assertTrue(service.isDocumentoTotalmenteAssinado(mapAssinaturas));
    }
    
}
