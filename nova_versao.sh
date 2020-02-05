#!/bin/bash
NOVA_VERSAO="$1"
mvn -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion="$NOVA_VERSAO"
mvn -f epp -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion="$NOVA_VERSAO"
mvn -f epp/bom -Pbuild:bom versions:set -DgenerateBackupPoms=false -DnewVersion="$NOVA_VERSAO"
