#!/bin/bash

MADSONIC_HOME="/Library/Application Support/Madsonic"

# Cleanup jetty.
#rm -rf  "$MADSONIC_HOME/jetty"

# Backup database.
if [ -e  "$MADSONIC_HOME/db" ]; then
  rm -rf "$MADSONIC_HOME/db.backup"
  cp -R  "$MADSONIC_HOME/db" "$MADSONIC_HOME/db.backup"
fi

