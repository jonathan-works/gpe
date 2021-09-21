#/bin/sh
cd $(dirname $0)
script_dirname=$(pwd -P)
cd ../..
base_repo_folder=$(pwd -P)
work_folder="$base_repo_folder/target/work"
cd "$script_dirname/../.."

if [ ! -e $work_folder/build.txt ] ; then
  echo "N EXISTE $work_folder/build.txt"
  exit 1
fi
source $work_folder/build.txt
if [ -z $tipoDoRepositorio ] ; then
  echo "Build não possui tipoDoRepositorio definido, refaça o build a partir de uma branch de incidente ou de projeto"
  exit 1
fi
source $HOME/.m2/infra-jenkins-config.txt

server_type='app-epp-generic-pgsql-v64'

DATA_TO_SEND='CONTEXTO=epp'
DATA_TO_SEND="$DATA_TO_SEND&REPOSITORY=infox-maven-evo-qa"
DATA_TO_SEND="$DATA_TO_SEND&GROUP=br.com.loglabdigital.epp"
DATA_TO_SEND="$DATA_TO_SEND&LIQUIBASE=liquibase-epp"
DATA_TO_SEND="$DATA_TO_SEND&VERSAO=$version"
DATA_TO_SEND="$DATA_TO_SEND&K8S_NAMESPACE=infox-servicos"
DATA_TO_SEND="$DATA_TO_SEND&CI_REGISTRY_IMAGE=app/loglab-$tipoDoRepositorio"
DATA_TO_SEND="$DATA_TO_SEND&K8S_DEPLOY=loglab-$tipoDoRepositorio"

curl -X POST \
-H "Authorization:Basic $authorization" \
-i "https://jenkins.infra.apps.infox.com.br/job/$server_type/job/master/buildWithParameters" \
 --data "$DATA_TO_SEND"