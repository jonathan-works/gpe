package br.com.infox.core.persistence;

import java.util.List;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.persistence.Id;

import org.hibernate.AnnotationException;

import br.com.infox.core.exception.RecursiveException;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

/**
 * Classe que gerencia a consistência dos valores no campo que possui o caminho
 * completo da recursividade na entidade.
 * 
 * @author Infox
 * 
 */
public final class RecursiveManager {

    private RecursiveManager() {
    }

    public static final String MSG_PARENT_EXCEPTION = "Este registro já está nesta hierarquia";
    private static final LogProvider LOG = Logging.getLogProvider(RecursiveManager.class);

    /**
     * Retorna uma string com o valor do campo com a anotação PathDescriptor
     * concatenado por '/' de todos os elementos superiores na árvore do
     * registro informado.
     * 
     * @param object Registro que se deseja obter o fullPath
     * @param sb informe um new StringBuilder()
     * @param dadField Nome do campo da entidade que representa o pai do field
     *        informado
     * @param pathDescriptorField Nome do campo da entidade que representa o id
     *        do field informado
     * @return
     */
    private static <E extends Recursive<E>> String getFullPath(E object,
            StringBuilder sb) {
        if (object != null) {
            getFullPath(object.getParent(), sb);
            sb.append(object.getPathDescriptor());
            sb.append("|");
        }
        return sb.toString();
    }

    private static <E extends Recursive<E>> String getFullPath(E object) {
        return getFullPath(object, new StringBuilder());
    }

    public static <E extends Recursive<E>> void refactor(E object) {
        try {
            if (verifyParent(object)) {
                throw new RecursiveException(MSG_PARENT_EXCEPTION);
            }
            refactorFieldPath(object);
        } catch (AnnotationException e) {
            LOG.error(".refactor()", e);
        }
    }

    /**
     * Método que atualiza o fullPath do registro informado no argumento e
     * modifica todos os fullPaths de seus dependentes na árvore.
     * 
     * @param object Registro que se deseja atualizar
     */
    private static <E extends Recursive<E>> void refactorFieldPath(E object) {
        try {
            setFullPath(object);

            List<E> fieldList = object.getChildList();
            if (fieldList != null) {
                for (E o : fieldList) {
                    refactorFieldPath(o);
                }
            }
        } catch (InvalidTargetObjectTypeException ex) {
            LOG.error(".refactorFieldPath()", ex);
        }
    }

    /**
     * Obtem através das anotações os nomes dos campos que serão necessários
     * para efetuar as atualizações.
     * 
     * @param object Registro que se deseja atualizar
     * @throws InvalidTargetObjectTypeException
     * @throws AnnotationException
     */
    public static <E extends Recursive<E>> void setFullPath(E object) throws InvalidTargetObjectTypeException {
        object.setHierarchicalPath(getFullPath(object));
    }

    /**
     * Verifica se o registro não está apontando para seu pai como ele mesmo.
     * 
     * @return True se possuir ele mesmo na hierarquia
     * @throws InvalidTargetObjectTypeException
     * @throws AnnotationException
     */
    private static <E extends Recursive<E>> boolean verifyParent(E object) {
        try {
            Integer id = (Integer) ComponentUtil.getAnnotatedAttributeValue(object, Id.class);
            return hasParentDuplicity(object, id);
        } catch (AnnotationException | InvalidTargetObjectTypeException e) {
            LOG.error(".verifyParent()", e);
        }
        return false;
    }

    private static <E extends Recursive<E>> boolean hasParentDuplicity(E o,
            Integer checkId) throws InvalidTargetObjectTypeException {
        E dad = o.getParent();
        if (dad != null) {
            Integer id = (Integer) ComponentUtil.getAnnotatedAttributeValue(dad, Id.class);
            if (id != null && id.equals(checkId)) {
                return true;
            }

            return hasParentDuplicity(dad, checkId);
        }
        return false;
    }

    public static <E extends Recursive<E>> boolean isFullPathEmpty(E object) {
        String currentFullPath = object.getHierarchicalPath();
        return currentFullPath == null || "".equals(currentFullPath);
    }

    /**
     * Método para inativar recursivamente todos os filhos do objeto passado
     * 
     * @param obj raiz da sub-árvore que será inativada
     */
    public static <E extends Recursive<E>> void recursiveInactivate(E obj) {
        ComponentUtil.setValue(obj, "ativo", Boolean.FALSE);

        List<E> childList = obj.getChildList();
        for (E child : childList) {
            recursiveInactivate(child);
        }
    }
}
