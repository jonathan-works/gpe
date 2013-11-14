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
package br.com.infox.ibpm.home;

import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.ibpm.dao.CepDAO;
import br.com.infox.ibpm.entity.Cep;
import br.com.infox.ibpm.entity.Endereco;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(EnderecoHome.NAME)
@Install(precedence = Install.FRAMEWORK)
public class EnderecoHome extends AbstractHome<Endereco> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "enderecoHome";
    
    private String searchCep;
    @In private CepDAO cepDAO;

    public String getSearchCep() {
        if ((searchCep == null || "_____-___".equals(searchCep)) && instance.getCep() != null) {
            searchCep = instance.getCep().getNumeroCep();
        }
        return searchCep;
    }

    public void setSearchCep(String searchCep) {
        this.searchCep = searchCep;
    }

    /**
     * Método que traz os dados de endereço do CEP informado (se houver em base)
     */
    public void setEndereco(Cep cep) {
        if (cep == null) {
            this.setInstance(new Endereco());
        } else {
            this.getInstance().setCep(cep);
            this.getInstance().setNomeEstado(cep.getMunicipio().getEstado().getEstado());
            this.getInstance().setNomeCidade(cep.getMunicipio().getMunicipio());
            this.getInstance().setNomeLogradouro(cep.getNomeLogradouro());
            this.getInstance().setNomeBairro(cep.getNomeBairro());
        }
    }
    
    public Cep getCep() {
        return getInstance().getCep();
    }
    
    public void loadEnderecoByCep(){
        if (searchCep != null){
            setEndereco(cepDAO.findCep(searchCep));
        }
    }

    /**
     * Função para checar se o cep é null
     * 
     * @return
     */
    public boolean checkCep() {
        if (this.getInstance().getCep() != null) {
            return true;
        }
        return false;
    }

    /**
     * Função que checa se existe algum campo de endereço preenchido. (Se algum
     * campo for diferente de vazio o campo cep passa a ser obrigatório) Caso
     * todos os campos estiverem vazios ou nulls retorna true e o cep não será
     * obrigatório Se os campos de endereço forem iguais a null retorna false
     * 
     * @return
     */
    public boolean checkEndereco() {
        return checkEndereco(getInstance());
    }

    public boolean checkEndereco(Endereco endereco) {
        if ((!Strings.isEmpty(endereco.getNomeLogradouro()))
                || (!Strings.isEmpty(endereco.getNomeBairro()))
                || (!Strings.isEmpty(endereco.getComplemento()))) {
            return true;
        }
        return false;
    }

    @Override
    protected Endereco createInstance() {
        setInstance(createEndereco());
        getInstance().setCep(new Cep());
        return instance;
    }

    @Override
    public String persist() {
        UsuarioLogin pessoaLogada = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");

        // setando data e hora da alteração
        getInstance().setDataAlteracao(new Date());
        getInstance().setUsuarioCadastrador(pessoaLogada);
        String persist = "";
        if (getInstance().getCorrespondencia() == null) {
            getInstance().setCorrespondencia(Boolean.FALSE);
        }
        if (checkCep()) {
            persist = super.persist();
            if (persist != null) {
                newInstance();
            }
        }
        return persist;
    }

    @Override
    public String update() {
        String update = "";
        if (checkCep()) {
            getInstance().setDataAlteracao(new Date());
            UsuarioLogin pessoaLogada = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");

            getInstance().setUsuarioCadastrador(pessoaLogada);

            update = super.update();
        }
        return update;
    }

    public static EnderecoHome instance() {
        return ComponentUtil.getComponent(EnderecoHome.NAME);
    }

    @Override
    public void newInstance() {
        searchCep = null;
        super.newInstance();
    }
    
    //Vindo do antigo AbstractEnderecoHome
    
    public void setEnderecoIdEndereco(Integer id) {
        setId(id);
    }

    public Integer getEnderecoIdEndereco() {
        return (Integer) getId();
    }

    protected Endereco createEndereco() {
        Endereco endereco = new Endereco();
        UsuarioHome usuarioHome = (UsuarioHome) Component.getInstance(
                "usuarioHome", false);
        if (usuarioHome != null) {
            endereco.setUsuario(usuarioHome.getDefinedInstance());
        }
        CepHome cepHome = (CepHome) Component.getInstance("cepHome", false);
        if (cepHome != null) {
            endereco.setCep(cepHome.getDefinedInstance());
        }
        return endereco;
    }

    @Override
    public String remove() {
        UsuarioHome usuario = (UsuarioHome) Component.getInstance(
                "usuarioHome", false);
        if (usuario != null) {
            usuario.getInstance().getEnderecoList().remove(instance);
        }
        CepHome cep = (CepHome) Component.getInstance("cepHome", false);
        if (cep != null) {
            cep.getInstance().getEnderecoList().remove(instance);
        }
        return super.remove();
    }

    @Override
    public String remove(Endereco obj) {
        setInstance(obj);
        String ret = super.remove();
        newInstance();
        return ret;
    }

    public List<Localizacao> getLocalizacaoList() {
        return getInstance() == null ? null : getInstance()
                .getLocalizacaoList();
    }

}
