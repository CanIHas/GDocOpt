package can.i.has.docopt

import groovy.util.logging.Commons

import static can.i.has.docopt.YesYouCan.*
import javax.script.ScriptException as DocoptException

/**
 * Custom implementation of conftest.py.
 */
class ConfTest extends GroovyTestCase{
    URL fixtureUrl
    ConfReader confReader

    void setUp(){
        fixtureUrl = new URL("https://raw.githubusercontent.com/docopt/docopt/master/testcases.docopt")
        confReader = new ConfReader()
    }



    void testFixtures(){
        confReader.eachFixture(fixtureUrl) { String doc, List<String> args, def expected, int i ->
            def out
            try {
                out = docopt(doc, args)
            } catch (DocoptException ignored) {
                out = "user-error"
            }
            assert out == expected

        }
    }
}
