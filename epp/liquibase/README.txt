Implementado e Testado com a versão 3.2.2 do Liquibase

Exemplo do comando:
    sh [executavel_liquibase] --defaultsFile=config/[(postgresql|sqlserver)*(-bin)].properties
--url="jdbc:sqlserver://172.20.1.235\SQLEXPRESS:1433;databaseName=[nome_base_dados];SelectMethod=cursor"
--username=[usuario] --password=[senha] [comando_liquibase]

