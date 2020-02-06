# Construção do projeto com os repositórios locais

mvn -s settings.xml clean package -Pbuild:all

# Construção do projeto com os repositórios locais sem executar os testes

mvn -s settings.xml clean package -Pbuild:all -DskipTests

# Atualizar versões
mvn -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
mvn -f epp -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
mvn -f epp/bom -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
