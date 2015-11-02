IIC2613-pacman
==============

Java project to develop AI for pacman. The project was used for the project
  held on [pacman-vs-ghosts.net](http://www.pacman-vs-ghosts.net/), but is
  currently unavailable.

Import
------
Eclipse can import this _existing project_ using the _git_ import dialog.

Working
-------
Visit the only 3 tasks (Eclipse>Window>Show View>Tasks). All should be clear
  from there.

Any modification outside your Controllers is not allowed directly, but may be
  proposed on a pull request.


Ideas
-----
Any source for ideas and code *must* be published here. Pull requests are
  encouraged.
   * [understanding-pac-man-ghost-behavior](http://gameinternals.com/post/2072558330/understanding-pac-man-ghost-behavior)
   * [Ms PacMan with MonteCarlo simulation](https://github.com/stewartml/montecarlo-pacman)



Rules
-----
The Contest differs on the usual settings with the following changes:
   * The contest will be run at the default _DELAY_ of 40ms and also on 400ms.
   * Only the first map will be played. Easing the offline computations.
   * Ghosts are allowed to choose *any* direction, including backwards. This
	     will require reviewing existing solutions.
   * Time and memory limits for Controllers are increased to 30s and 1GB to
	     exploit better the current hardware.
   * File and Network IO is forbidden.
   * Controllers code should each be under 1MB.

Additional rules:
   * Extra information *MUST* be added to the _Ideas_ section and
       made available through a pull request. Not complying with
       this will result in disqualification.
   * Modifications outside `pacman.entries` must be made through a pull request.
   * No now features will be reviewed 3 days before the deadline, only fixes
       up to 2 days after the deadline.

