package br.com.infox.epp.entrega.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.entrega.ModeloEntregaSearch;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;

@Stateless
public class ModeloEntregaRestService {

    @Inject
    private ModeloEntregaSearch modeloEntregaSearch;
    
    public List<Categoria> getCategoriasFilhas(String codigoItemPai, String codigoLocalizacao, Date date) {
        return convertToDTOList(modeloEntregaSearch.getCategoriasEItensComEntrega(codigoItemPai, codigoLocalizacao, date));
    }

    private List<Categoria> convertToDTOList(List<CategoriaEntregaItem> categoriaItems) {
        Map<CategoriaEntrega,Categoria> map = new HashMap<>();
        for (CategoriaEntregaItem item : categoriaItems) {
            CategoriaEntrega categoriaEntrega = item.getCategoriaEntrega();
            Categoria categoria = map.get(categoriaEntrega);
            if (categoria == null){
                categoria = new Categoria(categoriaEntrega.getCodigo(), categoriaEntrega.getDescricao());
                map.put(categoriaEntrega, categoria);
            }
            categoria.getItens().add(new Item(item.getCodigo(), item.getDescricao()));
        }
        
        return new ArrayList<>(map.values());
    }

}
