# HGBExample2

HGBExample2 is considerably more complex than HGBExample1.  It draws
the hive, has menu picks to zoom in/out, expand/contract (the size of
the hive), toggle the cell ID's on/off, and show a report.  one can
also move the hive about, and can pick cells which are filled with a color.

Zoom In/Out:  demo's change cell size.  (And note the lines quality
remains constant.)

Expand/Contract:  demo's add/remove rings  (add/remove cells).

Report: show the of number cell, roses and rose rings

Toggle numbers: toggles the display of cell ID's on/off

Translate: demo's how smoothly the hive translated.

Touch a cell:  demo's the locator and how quickly cells are found. (Touch
outside the hive to clear the filled cells.)

HGB is constrained to 60 rings (a setting in class Shared) allowing it to generated
74347 cells.  On my emulator, it still performs well, translates smoothly
and the locator finds cells quickly. If the cells are very small, the
locator has problems.

To see this:  expand the hive and zoom out (turn off the ID's).  Grow
the hive with multiple touches on "Expand".  Ask for a report occasionally.
Then, if it stops growing at 60 rings, you might try increasing
the limit maxRoseRings = 60, in class Shared, and see how many cells you
get on your device before it crashes or the response become intolerable.

