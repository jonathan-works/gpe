update tb_contribuinte as cont
set id_pessoa_fisica = pf.id_pessoa_fisica 
from tb_pessoa_fisica as pf
where cont.nr_cpf = pf.nr_cpf;
