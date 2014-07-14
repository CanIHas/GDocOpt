package can.i.has.python

import java.lang.reflect.Method

@Singleton
class CodeStringUtils {
    String pythonize(Object[] array){
        if (array==null)
            return "None"
        "[ "+ array.collect(this.&pythonize).join(", ") + " ]"
    }

    String pythonize(String string) {
        if (string==null)
            return "None"
        if (string.contains("\n"))  // if multiline string
            "'''$string'''"         // use ''' <text> '''
        else                        // if singleline string
            "'$string'"             // use ' <text> '
    }

    String pythonize(Map map){
        if (map==null)
            return "None"
        def pairs = []
        map.each { k, v ->
            pairs.add "${pythonize(k)}=${pythonize(v)}"
        }
        "dict(${pairs.join(", ")})"
    }

    String pythonize(List list) {
        pythonize(list.toArray())
    }

    String pythonize(File file){
        pythonize(file.toString())
    }

    String pythonize(URL url) {
        pythonize(url.toString())
    }

    String pythonize(Boolean bool){
        if (bool==null)
            return "None"
        bool? "True": "False"
    }

    String pythonize(Number number){
        if (number==null)
            return "None"
        "$number"
    }

    String pythonize(Object o) {
        if (o==null)
            return "None"
        def msg = "Cannot cast arbitrary object $o to python object!"

        msg += " Casting of following types to python object is supported: " +
            "${castable.collect {it as String}.join(", ")}"
        throw new UnsupportedOperationException(msg)
    }

    protected getCastable(){
        def supported = this.class.methods.findAll { Method method ->
            method.name=="pythonize"
        }.collect { Method method ->
            assert method.parameterTypes.size()==1
            method.parameterTypes[0]
        }
        supported.remove(Object)
        supported
    }
}
