#!/bin/bash

MADSONIC_HOME="/Library/Application Support/Madsonic"

chmod oug+rwx "$MADSONIC_HOME"
chown root:admin "$MADSONIC_HOME"

chmod oug+rx "$MADSONIC_HOME/transcode"
chown root:admin "$MADSONIC_HOME/transcode"

rm -rf "$MADSONIC_HOME/jetty"

echo Madsonic installation done
