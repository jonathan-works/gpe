update
	tb_servidor
set
	id_pessoa_fisica = pes.id_pessoa_fisica
from
	tb_servidor serv
inner join tb_pessoa_fisica pes on
	serv.nr_cpf = pes.nr_cpf;
