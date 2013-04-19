package br.com.infox.epa.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epa.entity.ImagemBin;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(ImagemBinHome.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class ImagemBinHome extends AbstractHome<ImagemBin> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "imagemBinHome";
	
	public static final ImagemBinHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	public String persist() {
		String ret = "";
		if (instance == null) {
			newInstance();
		}
		
		ret = super.persist();
		if (!"PERSISTED".equalsIgnoreCase(ret)) {
			FacesMessages.instance().add(Severity.ERROR,
					"Erro ao gravar a imagem.");
		}
		
		return ret;	
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if (instance == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"Nenhuma imagem selecionada.");
			return false;
		}
		String extensao = instance.getExtensao();
		if( !(extensao.equalsIgnoreCase("JPG") 
				|| extensao.equalsIgnoreCase("PNG") 
				|| extensao.equalsIgnoreCase("JPEG"))  ) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"A imagem deve ser do tipo JPG, PNG ou JPEG.");
			return false;
		}
		Integer tamanho = instance.getTamanho();
		if(tamanho != null && tamanho > 1572864){
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					"A imagem deve ter o tamanho máximo de 1.5MB!");
			return false;
		}
		return true;
	}
	
}
