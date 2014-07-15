package can.i.has.docopt

import static can.i.has.python.YesYouCan.*

class Docopt {

    static {
        modules.tryInspectingPythonRuntime()
        modules.fetchIfNeeded(
            "docopt",
            new URL("https://raw.githubusercontent.com/docopt/docopt/master/docopt.py")
        )
    }

    static Map<String, String> docopt(String doc, String[] argv=null,
                             boolean help=true, String version=null,
                             boolean optionsFirst=false) {
        def pyDoc = pythonize(doc)
        def pyArgv = pythonize(argv)
        def pyHelp = pythonize(help)
        def pyVersion = pythonize(version)
        def pyOptions = pythonize(optionsFirst)
        def script = interpreter.wrapExpression("from docopt import docopt",
            "docopt($pyDoc, $pyArgv, $pyHelp, $pyVersion, $pyOptions)")
        def result = interpreter.evaluate(script) as Map
        def out = [:]
        result.each {k, v -> out[k]=v}
        out
//        interpreter.evaluate(script) as Map
    }

    static Map<String, String> docopt(String doc, List<String> argv,
                             boolean help=true, String version=null,
                             boolean optionsFirst=false) {
        docopt(doc, argv as String[], help, version, optionsFirst)
    }
}