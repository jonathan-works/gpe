package br.com.infox.epp.access.dao;

import static br.com.infox.epp.access.query.PapelQuery.GET_PERFIL_TERMO_ADESAO;
import static br.com.infox.epp.access.query.PapelQuery.HAS_TO_SIGN_TERMO_ADESAO;
import static br.com.infox.epp.access.query.PapelQuery.ID_PAPEL_PARAM;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_IDENTIFICADORES;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_BY_LOCALIZACAO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PAPEIS_ORDEM_AFABETICA;
import static br.com.infox.epp.access.query.PapelQuery.PAPEL_BY_IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_CLASSIFICACAO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_IDENTIFICADOR;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_LISTA_IDENTIFICADORES;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_LOCALIZACAO;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_TIPO_MODELO_DOCUMENTO;
import static br.com.infox.epp.access.query.PapelQuery.PARAM_USUARIO;
import static br.com.infox.epp.access.query.PapelQuery.PERMISSOES_BY_PAPEL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Stateless
@AutoCreate
@Name(PapelDAO.NAME)
public class PapelDAO extends DAO<Papel> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "papelDAO";

    public List<Papel> getPapeisNaoAssociadosATipoModeloDocumento(
            TipoModeloDocumento tipoModeloDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_TIPO_MODELO_DOCUMENTO, tipoModeloDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_TIPO_MODELO_DOCUMENTO, parameters);
    }

    public List<Papel> getPapeisNaoAssociadosAClassificacaoDocumento(
            ClassificacaoDocumento classificacaoDocumento) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_CLASSIFICACAO_DOCUMENTO, classificacaoDocumento);
        return getNamedResultList(PAPEIS_NAO_ASSOCIADOS_A_CLASSIFICACAO_DOCUMENTO, parameters);
    }

    public Papel getPapelByIndentificador(String identificador) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_IDENTIFICADOR, identificador);
        return getNamedSingleResult(PAPEL_BY_IDENTIFICADOR, parameters);
    }

    public List<Papel> getPapeisByListaDeIdentificadores(
            List<String> identificadores) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LISTA_IDENTIFICADORES, identificadores);
        return getNamedResultList(PAPEIS_BY_IDENTIFICADORES, parameters);
    }

    public List<Papel> getPapeisDeUsuarioByLocalizacao(Localizacao localizacao) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_LOCALIZACAO, localizacao);
        return getNamedResultList(PAPEIS_BY_LOCALIZACAO, parameters);
    }

    public List<String> getListaDeNomesDosPapeis() {
        return getResultList("select distinct identificador from Papel", null);
    }

    public List<String> getListaPermissoes(Papel papel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_PAPEL_PARAM, papel.getIdPapel());
        return getNamedResultList(PERMISSOES_BY_PAPEL, parameters);
    }
    
    public boolean hasToSignTermoAdesao(UsuarioLogin usuario){
    	Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_USUARIO, usuario);
        return getNamedSingleResult(HAS_TO_SIGN_TERMO_ADESAO, parameters) != null;
    }
    
    public UsuarioPerfil getPerfilTermoAdesao(UsuarioLogin usuario){
    	Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_USUARIO, usuario);
        return getNamedSingleResult(GET_PERFIL_TERMO_ADESAO, parameters);
    }
    
    public List<Papel> getPapeisOrdemAlfabetica() {
        return getNamedResultList(PAPEIS_ORDEM_AFABETICA);
    }
}
