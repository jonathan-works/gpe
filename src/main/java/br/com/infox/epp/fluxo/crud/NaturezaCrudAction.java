package br.com.infox.epp.fluxo.crud;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.document.DocumentData;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.report.AutomaticReportController;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(NaturezaCrudAction.NAME)
public class NaturezaCrudAction extends AbstractCrudAction<Natureza, NaturezaManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "naturezaCrudAction";
    
    private DocumentData data;
    
    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setHasPartes(false);
    }

    @Override
    protected boolean isInstanceValid() {
        final Natureza natureza = getInstance();
        final Boolean hasPartes = natureza.getHasPartes();
        if (hasPartes == null) {
            return false;
        }
        if (hasPartes) {
            if (natureza.getTipoPartes() == null) {
                return false;
            }
            switch (natureza.getTipoPartes()) {
            case F:
                return natureza.getNumeroPartesFisicas() != null;
            case J:
                return natureza.getNumeroPartesJuridicas() != null;
            case A:
                return natureza.getNumeroPartesFisicas() != null || natureza.getNumeroPartesJuridicas() != null;
            default:
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void beforeSave() {
        final Natureza natureza = getInstance();
        if (!natureza.getHasPartes()) {
            natureza.setTipoPartes(null);
            natureza.setNumeroPartesFisicas(null);
            natureza.setNumeroPartesJuridicas(null);
        } else if (natureza.apenasPessoaFisica()) {
            natureza.setNumeroPartesJuridicas(null);
        } else if (natureza.apenasPessoaJuridica()) {
            natureza.setNumeroPartesFisicas(null);
        }
    }

    public ParteProcessoEnum[] getTiposDePartes() {
        return ParteProcessoEnum.values();
    }

    public void teste() throws IOException {
		ExecutorService executor = Executors.newFixedThreadPool(20);
		class A implements Runnable {
			private int id;
			public A(int id) {
				this.id = id;
			}
			
			@Override
			public void run() {
				try {
					HttpClient client = new HttpClient();
		    		HttpMethod method = new GetMethod("http://localhost:8080/epp-tcepe/RelacaoJulgamento/PautaDOE/pautadoe.seam?id=" + id);
		    		AutomaticReportController a = BeanManager.INSTANCE.getReference(AutomaticReportController.class);
		    		if (id % 2 == 0) {
		    			method.addRequestHeader(AutomaticReportController.KEY_HEADER_NAME, a.getKey().toString());
		    		}
		    		client.executeMethod(method);
					FileOutputStream fos = new FileOutputStream("/tmp/a/" + id + ".pdf");
					byte[] bytes = method.getResponseBody();
					fos.write(bytes, 0, bytes.length);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (int i = 0; i < 100; i++) {
			executor.execute(new A(i));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(999999, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public DocumentData getData() {
		return data;
	}
    
    public void setData(DocumentData data) {
		this.data = data;
	}
}
