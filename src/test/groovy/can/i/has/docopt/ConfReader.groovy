package can.i.has.docopt

import groovy.json.JsonSlurper
import groovy.transform.Canonical


class ConfReader {
    static enum State {
        PENDING,
        READ_DOC,
        READ_ARGS,
        READ_EXPECTED,
        EXPECT_END
    }

    static final JsonSlurper slurper = new JsonSlurper()

    void eachFixture(def source, Closure c) {
        new Run(source, c).eachFixture()
    }

    @Canonical
    protected static class Run {
        protected State state = State.PENDING
        def source  // it has to have eachLine(Closure)
        Closure c  // closure taking (String doc, List<String> args, def expected, int lastLineNo)

        String lastDocs
        List<String> lastArgs
        Object lastExpected
        int lineNo = 0

        void doLine(String line) {
            if (line.contains("#")){
                line = line.substring(0, line.indexOf("#")).trim()
            }
            switch (state) {
                case State.PENDING: doPending(line); break;
                case State.READ_DOC: doDoc(line); break;
                case State.READ_ARGS: doArgs(line); break;
                case State.READ_EXPECTED: doExpected(line); break;
                case State.EXPECT_END: doEnd(line); break;
                default: assert (lineNo || 1) && "Impossibru!" && false
            }
        }

        void eachFixture() {
            source.eachLine {
                doLine(it)
                lineNo++
            }
        }

        protected void doPending(String line) {
            if (line.trim()) {
                if ( line.startsWith('r"""') || line.startsWith("r'''") ) {
                    lastDocs = ''
                    state = State.READ_DOC
                    doLine(line.substring(4)) // remove trailing r""" or r'''
                } else if (line.startsWith(/$/)){
                    state = State.READ_ARGS
                    doLine(line)
                } else {
                    state = State.READ_EXPECTED
                    doLine(line)
                }
            } else {
                state = State.PENDING
            }
        }
        protected void doDoc(String line) {
            if (line.endsWith("'''") || line.endsWith('"""')) {
                if (lastDocs)
                    lastDocs += "\n"
                lastDocs += line.substring(0, line.size()-3) // remove ''' or """ from the end
                state = State.READ_ARGS
            }
            else
                lastDocs += "\n"+line
        }

        protected void doArgs(String line) {
            // assumed: there are no quoted args in fixtures
            // that means there are no lines like:
            // $ prog "a b"
            if (line.trim()) {
                assert line.startsWith(/$/)
                lastArgs = line.split(/\p{Z}+/).tail().tail() // drop "$", then "prog" (or equivalent)
                state = State.READ_EXPECTED
                lastExpected = ''
            }
        }

        protected void doExpected(String line) {
            if (line.trim()) {
                if (line == "\"user-error\"") {
                    lastExpected = "user-error"
                    state = State.EXPECT_END
                    // if fixture is malformed and "user-error" is not first line of "expected" section
                    // this will cause trouble
                } else {
                    lastExpected += line
                }
            } else {
                lastExpected = slurper.parseText(lastExpected)
                state = State.EXPECT_END
                doLine(line)
            }

        }

        protected void doEnd(String line) {
            assert !line.trim()
            c(lastDocs, lastArgs, lastExpected, lineNo)
            state = State.PENDING
        }
    }


}
