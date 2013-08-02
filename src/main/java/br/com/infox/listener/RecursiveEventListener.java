package br.com.infox.listener;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Id;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.annotations.HierarchicalPath;
import br.com.infox.annotations.Parent;
import br.com.infox.annotations.PathDescriptor;
import br.com.infox.annotations.manager.RecursiveManager;
import br.com.itx.exception.RecursiveException;
import br.com.itx.util.AnnotationUtil;
import br.com.itx.util.ComponentUtil;

/**
 * Classe listener do entityManager registrada no persistence.xml que
 * verifica se a entidade a ser inserida ou atualizada é recursiva,
 * sendo assim ela verifica se foi modificado o estado do Parent na 
 * hierarquia, caso sim o campo caminho completo irá ser refatorado.
 * @author Infox
 *
 */
public class RecursiveEventListener implements PreInsertEventListener, 
											   PreUpdateEventListener{

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RecursiveEventListener.class);

	public boolean onPreInsert(PreInsertEvent obj) {
		if(RecursiveManager.isRecursive(obj.getEntity())) {
			try {
				RecursiveManager.refactor(obj.getEntity());
				List<String> names = Arrays.asList(obj.getPersister()
										   .getClassMetadata().getPropertyNames());
				String pathFieldName = AnnotationUtil.getAnnotationField(obj
						.getEntity(), HierarchicalPath.class);
				obj.getState()[names.indexOf(pathFieldName)] = ComponentUtil.getValue
												(obj.getEntity(), pathFieldName);
			} catch (Exception e) {
				LOG.error(".onPreInsert()", e);
			}
		}
		return false;
	}

	public boolean onPreUpdate(PreUpdateEvent obj) {	
		if(RecursiveManager.isRecursive(obj.getEntity())) {
			try {
				List<String> names = Arrays.asList(obj.getPersister()
										   .getClassMetadata().getPropertyNames());
				String descriptorFieldName = AnnotationUtil.getAnnotationField
											(obj.getEntity(), PathDescriptor.class);				
				int descriptorPathIndex = names.indexOf(descriptorFieldName);
				if(!compareObj(obj.getOldState()[descriptorPathIndex], 
							   obj.getState()[descriptorPathIndex])) {
					RecursiveManager.refactor(obj.getEntity());
					return false;
				}
				int parentIndex = names.indexOf(AnnotationUtil.getAnnotationField
											   (obj.getEntity(), Parent.class));
				Object parent = obj.getState()[parentIndex];
				Object oldParent = obj.getOldState()[parentIndex];
				if(!compareById(parent, oldParent)) {
					RecursiveManager.refactor(obj.getEntity());
					String pathFieldName = AnnotationUtil.getAnnotationField(obj
												.getEntity(), HierarchicalPath.class);
					obj.getState()[names.indexOf(pathFieldName)] = ComponentUtil.getValue
														(obj.getEntity(), pathFieldName);
				}
			} catch (RecursiveException e) {
				throw e;
			} catch (Exception e) {
				LOG.error(".onPreUpdate()", e);
			}
		}
		return false;
	}

	private static boolean compareById(Object object1, Object object2) throws Exception {
		if (object1 == null || object2 == null) {
			return object1 == null && object2 == null;
		} else {
			String idFieldName = AnnotationUtil.getAnnotationField(object1, Id.class);
			Integer o1 = (Integer) ComponentUtil.getValue(object1, idFieldName);
			Integer o2 = (Integer) ComponentUtil.getValue(object2, idFieldName);
			return compareObj(o1, o2);
		}
	}
	
	private static boolean compareObj(Object object1, Object object2) {
		if (object1 == null) {
			return object2 == null;
		} else {
			return object1.equals(object2);
		}
	}
}
