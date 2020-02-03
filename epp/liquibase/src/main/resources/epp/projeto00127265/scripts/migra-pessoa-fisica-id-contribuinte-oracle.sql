UPDATE (SELECT
cont.id_pessoa_fisica AS idPFCont, pes.id_pessoa_fisica AS idPF
FROM
    tb_contribuinte cont
    INNER JOIN tb_pessoa_fisica pes ON
    cont.nr_cpf = pes.nr_cpf ) contribuinte
SET contribuinte.idPFCont = contribuinte.idPF;