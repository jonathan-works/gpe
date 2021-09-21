package br.com.infox.epp.access.component.tree;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

public class ParticipanteProcessoEntityNode extends EntityNode<ParticipanteProcesso> {

    private static final long serialVersionUID = 1L;

    public ParticipanteProcessoEntityNode(EntityNode<ParticipanteProcesso> parent, ParticipanteProcesso entity, String[] queryChildrenList) {
        super(parent, entity, queryChildrenList);
    }

    public ParticipanteProcessoEntityNode(String queryChildren) {
        super(queryChildren);
    }

    public ParticipanteProcessoEntityNode(String[] queryChildrenList) {
        super(queryChildrenList);
    }

    @Override
    public boolean canSelect() {
        return !TipoPessoaEnum.A.equals(getEntity().getPessoa().getTipoPessoa());
    }

    @Override
    protected EntityNode<ParticipanteProcesso> createChildNode(ParticipanteProcesso n) {
        return new ParticipanteProcessoEntityNode(this, n, getQueryChildrenList());
    }

    @Override
    protected EntityNode<ParticipanteProcesso> createRootNode(ParticipanteProcesso n) {
        return new ParticipanteProcessoEntityNode(null, n, getQueryChildrenList());
    }

}
