package can.i.has.docopt

import static can.i.has.docopt.Docopt.*

println docopt("""Usage: my_program.py [-hso FILE] [--quiet | --verbose] [INPUT ...]

-h --help    show this
-s --sorted  sorted output
-o FILE      specify output file [default: ./test.txt]
--quiet      print less text
--verbose    print more text

""", ["-s", "--verbose"])



println docopt("""Usage: yo [-h] [--fuck]

-h      Help kurwa
--fuck  Because you like it
""", ["-h"], false)
