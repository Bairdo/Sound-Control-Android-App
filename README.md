# Sound-Control-Server
Control Volume of Windows programs from Android. This is the Android application.

See bairdo/Sound-Control-Server for the Server application.

Not entirely sure if this currently works.

Basic functionality works (changing volume).
Server updates client of volume changes made outside the program(e.g. windows Volume Mixer, Keyboard)

# TODO List (not in any real order)
// todo update server so when new programs are added to audio (thing) they are also registered with the app. (without having to restart the server)
// todo receive icons from server.

// todo timestamp the udp packet, and only update if newer than the last

// todo have master vol (speaker static at top/bottom of page). DONE?

// todo have list so i can use it without the top of my screen. -  this is sort of covered by horozontal mode
// todo tidy up.
// todo rearrange-able list.
// todo nicer login screen.
// todo 'favourites' or profiles (probably need to save based on name.)
// todo make server a windows service (or whatever, so runs on startup)
// todo logging (server and app)
// todo command line args/config file (ports, logging) (server)
// todo save last used port and ip - DONE (maybe multiple ip and ports)(app).
// todo add a homepage widget
// todo add tests for both server and app
// todo ipv6

// todo error checking. what happens on invalid input from user (treat it gracefully)

// todo change speaker volume so works linearly (and in right to left direction).
// todo ability to send multiple updates in the same packet. (from server in  particular) will reduce the wasted overhead by ,making the packet data bigger.
// todo linux (ubuntu) version



Feature Requests are welcome