package br.com.infox.ibpm.swimlane;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.type.PooledActorType;

public class SwimlaneConfiguration {
    private static final Pattern PATTERN_EXPRESSION = Pattern.compile("#[{][^{}]+[}]");
    
    private List<PerfilTemplate> perfis = new ArrayList<>();
    private List<UsuarioLogin> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();
    private List<String> expressoes = new ArrayList<>();
    
    public static SwimlaneConfiguration fromPooledActorsExpression(String expression) {
        SwimlaneConfiguration configuration = new SwimlaneConfiguration();
        
        Matcher matcher = PATTERN_EXPRESSION.matcher(expression);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String expressao = matcher.group();
            if (isExpressaoPerfisAntigos(expressao)) {
                matcher.appendReplacement(sb, expressao.replace("#", "").replace("{", "").replace("}", ""));    
            } else {
                configuration.getExpressoes().add(expressao);
                matcher.appendReplacement(sb, "");
            }
        }
        matcher.appendTail(sb);
        
        String[] tokens = sb.toString().split(",");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                addItemFromPooledActor(token, configuration);
            }
        }
        
        return configuration;
    }
    
    private static boolean isExpressaoPerfisAntigos(String expressao) {
        return StringUtils.isNumeric(String.valueOf(expressao.charAt(2)));
    }
    
    private static void addItemFromPooledActor(String pooledActor, SwimlaneConfiguration configuration) {
        String[] tokens = pooledActor.split(":");
        if (tokens.length == 1) {
            PerfilTemplateManager perfilTemplateManager = BeanManager.INSTANCE.getReference(PerfilTemplateManager.class);
            if (StringUtils.isNumeric(tokens[0])) {
                configuration.getPerfis().add(perfilTemplateManager.find(Integer.valueOf(tokens[0])));
            } else {
                configuration.getPerfis().add(perfilTemplateManager.getPerfilTemplateByCodigo(tokens[0]));
            }
        } else if (tokens[0].startsWith(PooledActorType.USER.getValue())) {
            UsuarioLoginManager usuarioLoginManager = BeanManager.INSTANCE.getReference(UsuarioLoginManager.class);
            configuration.getUsuarios().add(usuarioLoginManager.getUsuarioLoginByLogin(tokens[1]));
        } else if (tokens[0].startsWith(PooledActorType.GROUP.getValue())) {
            PerfilTemplateManager perfilTemplateManager = BeanManager.INSTANCE.getReference(PerfilTemplateManager.class);
            LocalizacaoManager localizacaoManager = BeanManager.INSTANCE.getReference(LocalizacaoManager.class);
            
            String[] groupConfig = tokens[1].split("&");
            Grupo grupo = new Grupo(localizacaoManager.getLocalizacaoByCodigo(groupConfig[0]), perfilTemplateManager.getPerfilTemplateByCodigo(groupConfig[1]));
            configuration.getGrupos().add(grupo);
        } else {
            throw new IllegalArgumentException("Pooled Actor não reconhecido: " + pooledActor);
        }
    }

    public String toPooledActorsExpression() {
        StringBuilder sb = new StringBuilder();
        
        for (PerfilTemplate perfil : perfis) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(perfil.getCodigo());
        }
        
        for (UsuarioLogin usuario : usuarios) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(PooledActorType.USER.toPooledActorId(usuario.getLogin()));
        }
        
        for (Grupo grupo : grupos) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(PooledActorType.GROUP.getValue() + ":" + grupo.getLocalizacao().getCodigo() + "&" + grupo.getPerfil().getCodigo());
        }
        
        for (String expressao : expressoes) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(expressao);
        }
        
        return sb.toString();
    }

    public List<PerfilTemplate> getPerfis() {
        return perfis;
    }
    
    public void setPerfis(List<PerfilTemplate> perfis) {
        this.perfis = perfis;
    }
    
    public List<UsuarioLogin> getUsuarios() {
        return usuarios;
    }
    
    public void setUsuarios(List<UsuarioLogin> usuarios) {
        this.usuarios = usuarios;
    }
    
    public List<Grupo> getGrupos() {
        return grupos;
    }
    
    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }
    
    public List<String> getExpressoes() {
        return expressoes;
    }
    
    public void setExpressoes(List<String> expressoes) {
        this.expressoes = expressoes;
    }
    
    public static class Grupo {
        private Localizacao localizacao;
        private PerfilTemplate perfil;
        
        public Grupo() {
        }
        
        public Grupo(Localizacao localizacao, PerfilTemplate perfil) {
            this.localizacao = localizacao;
            this.perfil = perfil;
        }
        
        public Localizacao getLocalizacao() {
            return localizacao;
        }
        
        public void setLocalizacao(Localizacao localizacao) {
            this.localizacao = localizacao;
        }
        
        public PerfilTemplate getPerfil() {
            return perfil;
        }
        
        public void setPerfil(PerfilTemplate perfil) {
            this.perfil = perfil;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((localizacao == null) ? 0 : localizacao.hashCode());
            result = prime * result + ((perfil == null) ? 0 : perfil.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Grupo other = (Grupo) obj;
            if (localizacao == null) {
                if (other.localizacao != null)
                    return false;
            } else if (!localizacao.equals(other.localizacao))
                return false;
            if (perfil == null) {
                if (other.perfil != null)
                    return false;
            } else if (!perfil.equals(other.perfil))
                return false;
            return true;
        }
    }
}
