#!/bin/sh
cd $(dirname $0)
script_dirname=$(pwd -P)
cd ../..
base_repo_folder=$(pwd -P)
work_folder="$base_repo_folder/target/work"
cd "$script_dirname/../.."
rm -rf "$work_folder"
mkdir -p "$work_folder"

# CONFIGURAÇÃO DO MAVEN
MAVEN_OPTS="$MAVEN_OPTS -Djava.awt.headless=true"
MAVEN_OPTS="$MAVEN_OPTS -XX:+TieredCompilation"
MAVEN_OPTS="$MAVEN_OPTS -XX:TieredStopAtLevel=1"
MAVEN_OPTS="$MAVEN_OPTS -XX:+UseParallelGC"
MAVEN_OPTS="$MAVEN_OPTS -Dmaven.artifact.threads=8"
# MAVEN_OPTS="$MAVEN_OPTS -Dmaven.repo.local='$work_folder/.m2/repo'"
MAVEN_OPTS="$MAVEN_OPTS -Dgit-commit-id-plugin.useGitNative=true"

#URL NEXUS
URL_NEXUS="https://nexus.k8s.infox.com.br/repository"

#ATUALIZAR VERSÃO SNAPSHOT
find -name pom.xml -not -path */target/*  -not -path .git -exec git checkout -q '{}' \;
MAVEN_OPTS="$MAVEN_OPTS" mvn -q help:evaluate -Dexpression='project.version' -Doutput="$work_folder/version.txt"
current_maven_version=$(< $work_folder/version.txt)
epochSeconds="$(git log -n 1 --format='%ct')"
named_version="${current_maven_version/-SNAPSHOT/}"
named_version="${named_version}-a$epochSeconds"

# Atualização de versão
MAVEN_OPTS="$MAVEN_OPTS" mvn -s settings.xml -Pbuild:bom -V -U -e versions:set -DprocessAllModules=true -DgenerateBackupPoms=false -DnewVersion="${named_version}"

# BUILD VIA MAVEN
MAVEN_OPTS="$MAVEN_OPTS" mvn -s settings.xml -Pbuild:all  -V -U -e clean verify

# VALIDAÇÃO DE ESTILO, PMD E CMD
MAVEN_OPTS="$MAVEN_OPTS" mvn -s settings.xml -Pbuild:all -V -U -e checkstyle:checkstyle pmd:pmd pmd:cpd

branchName=$(git branch --show-current)
nomeDoRepositorio='infox-maven-stagingarea'
if [[ $branchName =~ ^projeto ]] ; then
    echo "$branchName PROJETO"
    nomeDoRepositorio='infox-maven-evo-qa'
    tipoDoRepositorio='qa-evo'
elif [[ $branchName =~ ^incidente ]] ; then
    echo "$branchName INCIDENTE"
    nomeDoRepositorio='infox-maven-man-qa'
    tipoDoRepositorio='qa-sup'
elif [ -z $branchName ] ; then
    echo "Foi possível determinar o nome da branch atual"
else
    echo "O nome de branch '$branchName' não é previsto para definir se é projeto ou incidente"
fi

deployFile() {
    local filePath="$2"
    local pomPath="$1"
    local extraArgs=""
    if [ ! -z $3 ] ; then
       extraArgs="-Dpackaging=$3"
    fi
    echo "Enviando $2 para $URL_NEXUS/$nomeDoRepositorio"
    MAVEN_OPTS="$MAVEN_OPTS" mvn -q deploy:deploy-file -DrepositoryId='infoxNexus' $extraArgs -Durl="$URL_NEXUS/$nomeDoRepositorio" -DpomFile="$pomPath" -Dfile="$filePath" || {
        echo "
+======================================================================
| Falha ao tentar fazer upload do artefato $filePath. Possíveis causas são:
| Falha na comunicação com o nexus $URL_NEXUS, por favor verifique se o mesmo está disponível.
| Não existe um repositório com o nome $nomeDoRepositorio no nexus.
| Artefato com a identificação única definida pelo POM $pomPath na versão $named_version já está disponibilizado no repositório $URL_NEXUS/$nomeDoRepositorio
+======================================================================";
    }
}

deployFile 'epp/jsf-gui/pom.xml' "epp/jsf-gui/target/epp-${named_version}.war"
deployFile 'epp/liquibase/pom.xml' "epp/liquibase/target/liquibase-epp-${named_version}.tar.gz" 'tar.gz'
touch $work_folder/build.txt
echo "branchName=$branchName
tipoDoRepositorio=$tipoDoRepositorio
nomeDoRepositorio=${nomeDoRepositorio}-implantacao
version=$named_version" > $work_folder/build.txt
