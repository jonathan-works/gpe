/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.ibpm.jbpm.util;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.itx.util.EntityUtil;

@Name(UserHandler.NAME)
@BypassInterceptors
@Scope(ScopeType.EVENT)
public class UserHandler {
    private static final LogProvider LOG = Logging
            .getLogProvider(UserHandler.class);
    public static final String NAME = "userHandler";
    
    private Integer idProcesso;
    private TaskInstance taskInstance;
    private String usuarioProcesso, usuarioTarefa;

    public String getNomeUsuario(TaskInstance task) {
        String login = task.getActorId();
        if (login == null || login.equals("")) {
            return getLocalizacao(task);
        }
        UsuarioLogin u = getUsuario(login);
        if (u != null) {
            return u.getLogin();
        }
        return null;
    }

    public String getActorIdTarefaAtual(Integer idProcesso) {
        if (this.idProcesso == null || !this.idProcesso.equals(idProcesso)) {
            try {
                
                String sql = "SELECT DISTINCT p.nm_pessoa "
                        + "FROM tb_usuario_login ul "
                        + "JOIN tb_pessoa p ON (ul.id_pessoa=p.id_pessoa) "
                        + "JOIN tb_usuario_taskinstance uti ON (ul.id_pessoa=uti.id_usuario_login) "
                        + "JOIN vs_situacao_processo sp ON (uti.id_taskinstance = sp.id_task_instance) "
                        + "WHERE id_processo=:idProcesso";
                Query query = EntityUtil.getEntityManager()
                        .createNativeQuery(sql)
                        .setParameter("idProcesso", idProcesso);
                this.idProcesso = idProcesso;
                this.usuarioProcesso = (String) query.getSingleResult();
            } catch (NoResultException e) {
                this.usuarioProcesso = "";
                LOG.warn("Não houve resultado. UserHandler.getActorIdTarefaAtual(Integer)");
            } catch (NonUniqueResultException e) {
                LOG.error("Múltiplos resultados. UserHandler.getActorIdTarefaAtual(Integer)", e);
            } catch (IllegalStateException e) {
                LOG.error("Estado inválido. UserHandler.getActorIdTarefaAtual(Integer)", e);
            }
        }
        return this.usuarioProcesso;
    }

    public String getUsuarioByTarefa(TaskInstance taskInstance) {
        if (this.taskInstance == null || !this.taskInstance.equals(taskInstance)) {
            try {
                String sql = "SELECT DISTINCT ul.ds_login FROM tb_usuario_login ul "
                        + "JOIN tb_usuario_taskinstance uti ON (uti.id_usuario_login = ul.id_pessoa) "
                        + "WHERE id_taskinstance = :idTaskInstance";
                Query query = EntityUtil.getEntityManager().createNativeQuery(sql)
                        .setParameter("idTaskInstance", taskInstance.getId());
                this.taskInstance = taskInstance;
                this.usuarioTarefa = (String) query.getSingleResult();
            } catch (NoResultException e) {
                this.usuarioTarefa = "";
                LOG.warn("Não houve resultado. UserHandler.getUsuarioByTarefa(TaskInstance)");
            } catch (NonUniqueResultException e) {
                LOG.error("Múltiplos resultados. UserHandler.getUsuarioByTarefa(TaskInstance)", e);
            } catch (IllegalStateException e) {
                LOG.error("Estado inválido. UserHandler.getUsuarioByTarefa(TaskInstance)", e);
            }
        }
        return this.usuarioTarefa;
    }

    public UsuarioLogin getUsuario(String login) {
        if (login == null || login.equals("")) {
            return null;
        }
        UsuarioLogin u = null;
        try {
            String sql = "select u from UsuarioLogin u where login=:login";
            u = (UsuarioLogin) EntityUtil.getEntityManager().createQuery(sql)
                    .setParameter("login", login).getSingleResult();
        } catch (NoResultException e) {
            LOG.warn("Usuário não encontrado. Login: " + login);
        } catch (Exception e) {
            LOG.error("Erro ao buscar usuário. Login: " + login, e);
        }
        return u;
    }

    private String getLocalizacao(TaskInstance task) {
        String localizacao = JbpmUtil.instance().getLocalizacao(task)
                .getCaminhoCompleto();
        return "Local: " + localizacao;
    }

}