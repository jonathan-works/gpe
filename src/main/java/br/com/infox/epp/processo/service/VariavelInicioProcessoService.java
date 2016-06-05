package br.com.infox.epp.processo.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.VariavelInicioProcesso;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.epp.processo.form.variable.value.ValueTypes;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VariavelInicioProcessoService extends PersistenceController {
    
    @Inject
    private VariavelInicioProcessoSearch variavelInicioProcessoSearch;
    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject 
    private DocumentoBinarioManager documentoBinarioManager;
    @Inject @GenericDao
    private Dao<VariavelInicioProcesso, Long> dao;
    
    public VariavelInicioProcesso getVariavel(Processo processo, String name) {
        return variavelInicioProcessoSearch.getVariavelInicioProcesso(processo, name);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setVariavel(Processo processo, String name, TypedValue typedValue) {
        VariavelInicioProcesso variavel = variavelInicioProcessoSearch.getVariavelInicioProcesso(processo, name);
        if (variavel == null) {
            variavel = new VariavelInicioProcesso();
            variavel.setName(name);
            variavel.setProcesso(processo);
            variavel.setType(typedValue.getType().getName());
            variavel.setValue(typedValue.getType().convertToStringValue(typedValue.getValue()));
            dao.persist(variavel);
        } else {
            variavel.setValue(typedValue.getType().convertToStringValue(typedValue.getValue()));
            dao.update(variavel);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAll(Processo processo) {
        List<VariavelInicioProcesso> variaveis = variavelInicioProcessoSearch.findAll(processo);
        for (VariavelInicioProcesso variavel : variaveis) {
            ValueType valueType = ValueTypes.create(variavel.getType());
            if (valueType == ValueType.FILE) {
                Integer idDocumento = (Integer) variavel.getTypedValue();
                if (idDocumento != null) {
                    Documento documento = documentoManager.find(idDocumento);
                    documentoManager.remove(documento);
                    documentoBinManager.remove(documento.getDocumentoBin());
                    if (documento.getDocumentoBin().isBinario()) {
                        documentoBinarioManager.remove(documento.getDocumentoBin().getId());
                    }
                }
            }
            dao.remove(variavel);
        }
    }
}
