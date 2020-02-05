UPDATE (SELECT
serv.id_pessoa_fisica AS idPFServ, pes.id_pessoa_fisica AS idPF
FROM
    tb_servidor serv
    INNER JOIN tb_pessoa_fisica pes ON
    serv.nr_cpf = pes.nr_cpf ) servidor
SET servidor.idPFServ = servidor.idPF;