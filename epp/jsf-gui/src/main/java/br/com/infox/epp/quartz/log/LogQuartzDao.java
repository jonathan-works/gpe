package br.com.infox.epp.quartz.log;

import javax.ejb.Stateless;

import br.com.infox.cdi.dao.Dao;

@Stateless
public class LogQuartzDao extends Dao<LogQuartz, Long>{

	public LogQuartzDao() {
		super(LogQuartz.class);
	}

	public void	inserir(LogQuartz logQuartz) {
		persist(logQuartz);
	}
	
	public LogQuartz atualizar(LogQuartz logQuartz) {
		return update(logQuartz);
	}
}
