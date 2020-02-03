update
	tb_contribuinte
set
	id_pessoa_fisica = pes.id_pessoa_fisica
from
	tb_contribuinte cont
inner join tb_pessoa_fisica pes on
	cont.nr_cpf = pes.nr_cpf;
