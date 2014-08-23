#!/bin/sh

###################################################################################
# Shell script for starting Madsonic.  See http://madsonic.org.
###################################################################################

MADSONIC_HOME=/var/madsonic
MADSONIC_HOST=0.0.0.0
MADSONIC_PORT=4040
MADSONIC_HTTPS_PORT=0
MADSONIC_CONTEXT_PATH=/
MADSONIC_INIT_MEMORY=192
MADSONIC_MAX_MEMORY=384
MADSONIC_PIDFILE=
MADSONIC_DEFAULT_MUSIC_FOLDER=/var/media/artists
MADSONIC_DEFAULT_UPLOAD_FOLDER=/var/media/incoming
MADSONIC_DEFAULT_PODCAST_FOLDER=/var/media/podcast
MADSONIC_DEFAULT_PLAYLIST_IMPORT_FOLDER=/var/media/playlists/import
MADSONIC_DEFAULT_PLAYLIST_EXPORT_FOLDER=/var/media/playlists/export
MADSONIC_DEFAULT_PLAYLIST_BACKUP_FOLDER=/var/media/playlists/backup
MADSONIC_DEFAULT_TIMEZONE=
quiet=0

usage() {
    echo "Usage: madsonic.sh [options]"
    echo "  --help               This small usage guide."
    echo "  --home=DIR           The directory where Madsonic will create files."
    echo "                       Make sure it is writable. Default: /var/madsonic"
    echo "  --host=HOST          The host name or IP address on which to bind Madsonic."
    echo "                       Only relevant if you have multiple network interfaces and want"
    echo "                       to make Madsonic available on only one of them. The default value"
    echo "                       will bind Madsonic to all available network interfaces. Default: 0.0.0.0"
    echo "  --port=PORT          The port on which Madsonic will listen for"
    echo "                       incoming HTTP traffic. Default: 4040"
    echo "  --https-port=PORT    The port on which Madsonic will listen for"
    echo "                       incoming HTTPS traffic. Default: 0 (disabled)"
    echo "  --context-path=PATH  The context path, i.e., the last part of the Madsonic"
    echo "                       URL. Typically '/' or '/madsonic'. Default '/'"
    echo "  --init-memory=MB     The memory initial size (Init Java heap size) in megabytes."
    echo "                       Default: 192"
    echo "  --max-memory=MB      The memory limit (max Java heap size) in megabytes."
    echo "                       Default: 384"
    echo "  --pidfile=PIDFILE    Write PID to this file. Default not created."
    echo "  --quiet              Don't print anything to standard out. Default false."
    echo "  --default-music-folder=DIR           Configure Madsonic to use this folder for music.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/artists'"
    echo "  --default-upload-folder=DIR          Configure Madsonic to use this folder for music.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/incoming'"
    echo "  --default-podcast-folder=DIR         Configure Madsonic to use this folder for Podcasts.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/podcast'"
    echo "  --default-playlist-import-folder=DIR Configure Madsonic to use this folder for playlist import.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/playlists/import'"
    echo "  --default-playlist-export-folder=DIR Configure Madsonic to use this folder for playlist export.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/playlists/export'"
    echo "  --default-playlist-backup-folder=DIR Configure Madsonic to use this folder for playlist backup.  This option "
    echo "                                       only has effect the first time Madsonic is started. Default '/var/media/playlists/backup'"
    echo "  --timezone=Zone/City                 Configure Madsonic to use other timezone for time correction"
    echo "                                       Example 'Europe/Vienna', 'US/Central', 'America/New_York'"
    exit 1
}

# Parse arguments.
while [ $# -ge 1 ]; do
    case $1 in
        --help)
            usage
            ;;
        --home=?*)
            MADSONIC_HOME=${1#--home=}
            ;;
        --host=?*)
            MADSONIC_HOST=${1#--host=}
            ;;
        --port=?*)
            MADSONIC_PORT=${1#--port=}
            ;;
        --https-port=?*)
            MADSONIC_HTTPS_PORT=${1#--https-port=}
            ;;
        --context-path=?*)
            MADSONIC_CONTEXT_PATH=${1#--context-path=}
            ;;
        --init-memory=?*)
            MADSONIC_INIT_MEMORY=${1#--init-memory=}
            ;;
        --max-memory=?*)
            MADSONIC_MAX_MEMORY=${1#--max-memory=}
            ;;
        --pidfile=?*)
            MADSONIC_PIDFILE=${1#--pidfile=}
            ;;
        --quiet)
            quiet=1
            ;;
        --default-music-folder=?*)
            MADSONIC_DEFAULT_MUSIC_FOLDER=${1#--default-music-folder=}
            ;;
        --default-upload-folder=?*)
            MADSONIC_DEFAULT_UPLOAD_FOLDER=${1#--default-upload-folder=}
            ;;
        --default-podcast-folder=?*)
            MADSONIC_DEFAULT_PODCAST_FOLDER=${1#--default-podcast-folder=}
            ;;
        --default-playlist-import-folder=?*)
            MADSONIC_DEFAULT_PLAYLIST_IMPORT_FOLDER=${1#--default-playlist-import-folder=}
            ;;
        --default-playlist-export-folder=?*)
            MADSONIC_DEFAULT_PLAYLIST_EXPORT_FOLDER=${1#--default-playlist-export-folder=}
            ;;
        --default-playlist-backup-folder=?*)
            MADSONIC_DEFAULT_PLAYLIST_BACKUP_FOLDER=${1#--default-playlist-backup-folder=}
            ;;
		--timezone=?*)
           MADSONIC_DEFAULT_TIMEZONE=${1#--timezone=}
           ;;
        *)
            usage
            ;;
    esac
    shift
done

# Use JAVA_HOME if set, otherwise assume java is in the path.
JAVA=java
if [ -e "${JAVA_HOME}" ]
    then
    JAVA=${JAVA_HOME}/bin/java
fi

# Create Madsonic home directory.
mkdir -p ${MADSONIC_HOME}
LOG=${MADSONIC_HOME}/madsonic_sh.log
rm -f ${LOG}

cd $(dirname $0)
if [ -L $0 ] && ([ -e /bin/readlink ] || [ -e /usr/bin/readlink ]); then
    cd $(dirname $(readlink $0))
fi

${JAVA} -Xms${MADSONIC_INIT_MEMORY}m -Xmx${MADSONIC_MAX_MEMORY}m \
  -Dmadsonic.home=${MADSONIC_HOME} \
  -Dmadsonic.host=${MADSONIC_HOST} \
  -Dmadsonic.port=${MADSONIC_PORT} \
  -Dmadsonic.httpsPort=${MADSONIC_HTTPS_PORT} \
  -Dmadsonic.contextPath=${MADSONIC_CONTEXT_PATH} \
  -Dmadsonic.defaultMusicFolder=${MADSONIC_DEFAULT_MUSIC_FOLDER} \
  -Dmadsonic.defaultUploadFolder=${MADSONIC_DEFAULT_UPLOAD_FOLDER} \
  -Dmadsonic.defaultPodcastFolder=${MADSONIC_DEFAULT_PODCAST_FOLDER} \
  -Dmadsonic.defaultPlaylistImportFolder=${MADSONIC_DEFAULT_PLAYLIST_IMPORT_FOLDER} \
  -Dmadsonic.defaultPlaylistExportFolder=${MADSONIC_DEFAULT_PLAYLIST_EXPORT_FOLDER} \
  -Dmadsonic.defaultPlaylistBackupFolder=${MADSONIC_DEFAULT_PLAYLIST_BACKUP_FOLDER} \
  -Duser.timezone=${MADSONIC_DEFAULT_TIMEZONE} \
  -Djava.awt.headless=true \
  -verbose:gc \
  -jar madsonic-booter.jar > ${LOG} 2>&1 &

# Write pid to pidfile if it is defined.
if [ $MADSONIC_PIDFILE ]; then
    echo $! > ${MADSONIC_PIDFILE}
fi

if [ $quiet = 0 ]; then
    echo Started Madsonic [PID $!, ${LOG}]
fi

