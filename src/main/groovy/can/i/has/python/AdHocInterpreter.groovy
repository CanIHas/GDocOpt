package can.i.has.python

import static can.i.has.python.YesYouCan.modules

import groovy.transform.Canonical

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import java.util.concurrent.Callable

@Singleton
@Canonical
class AdHocInterpreter {
    def eval(String setup, String expr){
        evaluate(wrapExpression(setup, expr))
    }

    def evaluate(String toEval) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("python");
        engine.eval(toEval)
        Callable callable = (engine as Invocable).getInterface(Callable)
        callable()
    }

    String wrapExpression(String setup, String expr) {
        def lines = setup.readLines()
        assert lines.size()>=1
        def out = """${modules.pathSnippet}

def call():
${lines.collect{"    $it"}.join("\n")}
"""

        out += "    return "+expr
        out
    }
}
