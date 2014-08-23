Name:           madsonic
Version:        @VERSION@
Release:        @BUILD_NUMBER@
Summary:        A web-based music streamer, jukebox and Podcast receiver

Group:          Applications/Multimedia
License:        GPLv3
URL:            http://madsonic.org

%description
Madsonic is a web-based music streamer, jukebox and Podcast receiver,
providing access to your music collection wherever you are. Use it
to share your music with friends, or to listen to your music while away
from home.

Apps for Android, iPhone and Windows Phone are also available.

Java 1.7 or higher is required to run Madsonic.

Madsonic can be found at http://madsonic.org

%files
%defattr(644,root,root,755)
/usr/share/madsonic/madsonic-booter.jar
/usr/share/madsonic/madsonic.war
%attr(755,root,root) /usr/share/madsonic/madsonic.sh
%attr(755,root,root) /etc/init.d/madsonic
%attr(755,root,root) /var/madsonic/transcode/Audioffmpeg
%attr(755,root,root) /var/madsonic/transcode/ffmpeg
%attr(755,root,root) /var/madsonic/transcode/lame
%attr(755,root,root) /var/madsonic/transcode/xmp
%config(noreplace) /etc/sysconfig/madsonic

%pre
# Stop Madsonic service.
if [ -e /etc/init.d/madsonic ]; then
  service madsonic stop
fi

# Backup database.
if [ -e /var/madsonic/db ]; then
  rm -rf /var/madsonic/db.backup
  cp -R /var/madsonic/db /var/madsonic/db.backup
fi

exit 0

%post
ln -sf /usr/share/madsonic/madsonic.sh /usr/bin/madsonic
chmod 750 /var/madsonic

# Clear jetty cache.
rm -rf /var/madsonic/jetty

# For SELinux: Set security context
chcon -t java_exec_t /etc/init.d/madsonic 2>/dev/null

# Configure and start Madsonic service.
chkconfig --add madsonic
service madsonic start

exit 0

%preun
# Only do it if uninstalling, not upgrading.
if [ $1 = 0 ] ; then

  # Stop the service.
  [ -e /etc/init.d/madsonic ] && service madsonic stop

  # Remove symlink.
  rm -f /usr/bin/madsonic

  # Remove startup scripts.
  chkconfig --del madsonic

fi

exit 0

