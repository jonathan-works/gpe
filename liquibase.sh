#!/bin/bash

MODULES=(epp epp-julgamento epp-conselho)

if [ $(git rev-parse --show-toplevel | rev | cut -f 1 -d / | rev) != 'epp' ]; then
    cd $(git rev-parse --show-toplevel)
    cd ..
fi

ROOT=$(git rev-parse --show-toplevel)
echo $ROOT

#for i in "${MODULES[@]}"; do
#    cd $ROOT/submodules/$i/liquibase
#    mvn clean install -U -P build:bom -f ../bom/pom.xml
#    mvn clean install -U
#done

cd $ROOT/epp/liquibase
mvn clean package -U
#bash /home/gustavomarcone/Dev/repository/epp/epp/liquibase/target/liquibase-epp-3.2.27-SNAPSHOT/liquibase-epp -s 'postgres' -d epp --host localhost --port 5432 -u postgres --action update
