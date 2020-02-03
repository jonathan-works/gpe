update tb_servidor as serv
set id_pessoa_fisica = pf.id_pessoa_fisica 
from tb_pessoa_fisica as pf
where serv.nr_cpf = pf.nr_cpf;
