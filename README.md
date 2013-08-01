Archis
======

(c)2001-2003 Adam Ierymenko

Archis is a plugin-based artificial life simulator / evolutionary
programming engine written in Java.

The [original Archis homepage is still online here](http://adam.ierymenko.name/archis/).

It's a bit old. I wrote it many years ago while I was in college to
prototype ideas for translating concepts from biology to computer
science. I didn't realize until I'd already gotten an earlier version
of this up and running that I was duplicating or at least running
alongside work like this:

 * [Avida](http://avida.devosoft.org)
 * [Tierra](http://life.ou.edu/tierra/)

This didn't end my fascination with evolutionary computation by
any means. In fact, I was overjoyed to find such things and learn
that I was not in fact nuts. :)

Later I wrote a much smaller program called [nanopond](http://adam.ierymenko.name/nanopond.shtml)
in plain vanilla C with the goal of stripping away the complexity and
revealing alife/EC systems of this sort in their purest form.

Since it's such old code, it's missing some modern Java idioms such
as templates. But it still builds and runs just fine with Java 7,
and boy does it run fast on today's machines! It's multithreaded, so
I was able to run it on my quad-core here and get rather amazing
performance.

I've put it online in the interest of putting up some of my more
interesting old code on GitHub. Enjoy!

--

To run Archis, just start the application and create a new simulation
from the File menu. You can have more than one simulation going at
once.

Archis simulates virtual Turing-complete organisms in a simulated
environment. The characteristics of this environment are defined by
plugins. Some plugins you might want to try right away are Landscape2D
(imposes a 2D grid and lets you see the little buggers) and
GenesisCondition (generates random genomes until one can self-replicate).

Some of the other conditions are reward functions, so plugging these
in can let you experiment with training your bugs to do things like
output Fibonacci sequences. There's a relatively simple Java interface
that you could use to do more experiments if you wanted.

I might write more docs in the future if I get around to it. :)

--

License: I'm releasing this code under the GNU General Public License
version 3. I haven't gotten around to installing that license everywhere,
but assume those are the terms if anyone wants to use this for
something.
