GDocOpt
=======

Literal port of python DocOpt library, using Jython.

Subpackage can.i.has.python will be moved to another project in the future, as I plan to create more
literal ports of different python libraries.

I took python test suite ([test_docopt.py](https://github.com/docopt/docopt/blob/master/test_docopt.py),
 [testcases.docopt](https://github.com/docopt/docopt/blob/master/testcases.docopt)
and custom implementation of reader of the latter) and literally rewritten all tests of form:

> assert docopt(...) == ...

or

> with raises(...):
>     docopt(...)

to Groovy Test Suite.

Whenever I say that I took some file (module, test fixture, whatever), I mean
[commit #1937a1c](https://github.com/docopt/docopt/commit/1937a1c9041e0f580d2890d38bb71c0a0623847f)
unless stated differently.
