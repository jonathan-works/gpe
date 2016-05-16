package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;

public interface FileTypeValue extends TypedValue {
    
    Documento getValue();
    
    Class<Documento> getType();
    
    String getName();
    
    String getExtension();
    
    String getDescription();
    
    ClassificacaoDocumento getClassificacaoDocumento();
}
