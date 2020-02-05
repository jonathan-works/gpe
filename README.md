# Construção do projeto com os repositórios locais

mvn -s settings.xml clean package -Pbuild:all

# Atualizar versões
mvn -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
mvn -f epp -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
mvn -f epp/bom -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion='NOVA_VERSAO'
