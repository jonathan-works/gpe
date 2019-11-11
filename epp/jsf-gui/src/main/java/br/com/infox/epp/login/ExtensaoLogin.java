package br.com.infox.epp.login;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExtensaoLogin {
    
    public List<String> getExtensoes(){
        return new ArrayList<>();
    }
    
}
