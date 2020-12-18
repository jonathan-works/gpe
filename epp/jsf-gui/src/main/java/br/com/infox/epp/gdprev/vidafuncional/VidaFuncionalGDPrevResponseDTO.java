package br.com.infox.epp.gdprev.vidafuncional;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VidaFuncionalGDPrevResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer total;
    private final Integer pagina;
    private List<DocumentoVidaFuncionalDTO> documentos;
}
