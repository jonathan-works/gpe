package br.com.infox.liquibase.processo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class PreencherSenhaAcessoTodosProcessos implements CustomTaskChange {

	 private JdbcConnection connection;
	 
	@Override
	public String getConfirmationMessage() {
		return "Adicionado senhas de acesso em todos os processo que ainda não possuiam";
	}

	@Override
	public void setUp() throws SetupException {
	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
	}

	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}

	@Override
	public void execute(Database database) throws CustomChangeException {
		connection = (JdbcConnection) database.getConnection();
		try {
			inserirSenhaAcessoProcessos();
            database.commit();
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
	}
	
	private void inserirSenhaAcessoProcessos() throws DatabaseException, SQLException {
		for(Number idProcesso : getListaIdProcessosSemSenhaAcesso()) {
			PreparedStatement updateProcessoSenhaAcesso = connection.prepareStatement("UPDATE tb_processo SET ds_senha_acesso = ? WHERE id_processo = ? ");
			updateProcessoSenhaAcesso.setObject(1, getSenhaAleatoria());
			updateProcessoSenhaAcesso.setObject(2, idProcesso.longValue());
			updateProcessoSenhaAcesso.execute();
			updateProcessoSenhaAcesso.close();
		}
	}
	
	private List<Number> getListaIdProcessosSemSenhaAcesso() throws DatabaseException, SQLException{
		PreparedStatement statement = connection.prepareStatement("SELECT id_processo FROM tb_processo WHERE ds_senha_acesso is null;");
        List<Number> listaIdProcessoSemSenha = new ArrayList<>();
        statement.execute();
        ResultSet rs = statement.getResultSet();
        while (rs.next()) {
        	listaIdProcessoSemSenha.add((Number) rs.getObject(1));
        }
        rs.close();
        statement.close();
        return listaIdProcessoSemSenha;
	}
	
	private String getSenhaAleatoria() {
		String fonte = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890";
		StringBuilder senhaGerada = new StringBuilder();
		Random rand = new Random();
		for(int i = 0; i < 15; i++) {
			senhaGerada.append(fonte.charAt(rand.nextInt(62)));
		}
		return senhaGerada.toString();
	}

}
