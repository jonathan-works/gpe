package br.com.infox.epp.processo.action;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

public class NaturezaCategoriaFluxoItem {
    
    private String id;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Item item;
    
    public NaturezaCategoriaFluxoItem(NaturezaCategoriaFluxo naturezaCategoriaFluxo, Item item) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
        this.item = item;
        this.id = generateId(); 
    }

    private String generateId() {
        String id = String.valueOf(naturezaCategoriaFluxo.getIdNaturezaCategoriaFluxo());
        if (item != null) {
            id += String.valueOf(item.getIdItem());
        }
        return id;
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }
    
    public Item getItem() {
        return item;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof NaturezaCategoriaFluxoItem))
            return false;
        NaturezaCategoriaFluxoItem other = (NaturezaCategoriaFluxoItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
