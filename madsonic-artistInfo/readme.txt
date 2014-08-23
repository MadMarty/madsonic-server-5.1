About

This "mod" adds a small list to your default subsonic web interface with suggested artists fetched from Last.fm.

The reason it uses PHP on the server-side to look up similar artists is because it was easier to develop and easier to set up as it doesn't require the user to re-compile subsonic.

I will attempt to run the PHP script on my university's server as long as I can, but if you can, it would be nice if you could set up your own.

INSTALL

Open the file: Client/scripts.js
Add everything under /// Add similar artists to your /var/subsonic/jetty/<num>/webapp/script/scripts.js file
Put the Client/similar_artists directory from this package in /var/subsonic/jetty/<num>/webapp/script/. (The result should be /var/subsonic/jetty/<num>/webapp/script/similar_artists/)
Optional: If you'd like to setup your own similar artists server

Upload Server from this package to any webserver running PHP5.
Open similar_artists/similar_artists.js and change the url var to point to your server.
Known limitations

License: MIT

