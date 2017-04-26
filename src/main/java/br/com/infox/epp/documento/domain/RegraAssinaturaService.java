package br.com.infox.epp.documento.domain;

import javax.ejb.Stateless;

import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.type.TipoAssinaturaEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Stateless
public class RegraAssinaturaService {

    public boolean assinadoPor(Assinavel assinavel, Papel papel){
    	for(Assinatura assinaturaDocumento : assinavel.getAssinaturas()){
    		if (assinaturaDocumento.getPapel().equals(papel)){
    			return true;
    		}
    	}
    	return false;
    }

    public boolean assinadoPor(Assinavel assinavel, PessoaFisica pessoa){
        for(Assinatura assinaturaDocumento : assinavel.getAssinaturas()){
            if (assinaturaDocumento.getPessoaFisica().equals(pessoa)){
                return true;
            }
        }
        return false;
    }

    public boolean permiteAssinaturaPor(Assinavel assinavel, Papel papel) {
    	for (RegraAssinatura regraAssinatura : assinavel.getRegrasAssinatura()){
    		if (regraAssinatura.getPapel().equals(papel) 
    				&& regraAssinatura.getTipoAssinatura() != TipoAssinaturaEnum.P){
    			return true;
    		}
    	}
    	return false;
    }

    public boolean permiteAssinatura(Assinavel assinavel) {
    	for (RegraAssinatura tipoProcessoDocumentoPapel : assinavel.getRegrasAssinatura()){
    		if (tipoProcessoDocumentoPapel.getTipoAssinatura() != TipoAssinaturaEnum.P){
    			return true;
    		}
    	}
    	return false;
    }

    public boolean permiteAssinaturaPor(Assinavel assinavel, PessoaFisica pessoaFisica, Papel papel) {
        boolean pessoaAssinouDocumento = assinadoPor(assinavel, pessoaFisica);
        boolean papelAssinouDocumento = assinadoPor(assinavel, papel);
        boolean permiteAssinaturaMultipla = permiteAssinaturaMultipla(assinavel, papel);
        
        return permiteAssinaturaPor(assinavel, papel) && !pessoaAssinouDocumento && (permiteAssinaturaMultipla || !papelAssinouDocumento);
    }

    public boolean possuiAssinaturaSuficiente(Assinavel assinavel) {
    	for (RegraAssinatura regraAssinatura : assinavel.getRegrasAssinatura()) {
    		if (regraAssinatura.getTipoAssinatura() == TipoAssinaturaEnum.S
    				&& assinadoPor(assinavel, regraAssinatura.getPapel())) {
    			return true;
    		}
    	}
    	return false;
    }

    public boolean assinaturaObrigatoria(Assinavel assinavel, Papel papel) {
    	boolean existeObrigatoria=false;
    	TipoAssinaturaEnum tipoAssinaturaEncontrado=null;
        for (RegraAssinatura regraAssinatura : assinavel.getRegrasAssinatura()) {
            if (TipoAssinaturaEnum.O.equals(regraAssinatura.getTipoAssinatura()))
                existeObrigatoria |= true;
            
            if (regraAssinatura.getPapel().equals(papel)) {
                tipoAssinaturaEncontrado = regraAssinatura.getTipoAssinatura();
            }
        }
    	return TipoAssinaturaEnum.O.equals(tipoAssinaturaEncontrado) || (TipoAssinaturaEnum.S.equals(tipoAssinaturaEncontrado) && !existeObrigatoria);
    }

    public boolean permiteAssinaturaMultipla(Assinavel assinavel, Papel papel) {
        for (RegraAssinatura regraAssinatura : assinavel.getRegrasAssinatura()) {
            if (regraAssinatura.getPapel().equals(papel)) {
                return regraAssinatura.getAssinaturasMultiplas();
            }
        }
        
        return false;
    }
    
}