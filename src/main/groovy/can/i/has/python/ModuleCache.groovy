package can.i.has.python

import groovy.transform.Canonical
import groovy.transform.InheritConstructors
import groovy.util.logging.Commons

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@Singleton(strict = false)
@Commons
class ModuleCache {
    protected File cacheTempDir
    protected final Set<File> moduleDirs
    boolean autoDelete = false
    protected List<String> alreadyTriedCommands = []

    ModuleCache(){
        moduleDirs = [] as Set
        cacheTempDir = null
        def propEnum = this.class.classLoader.getResources("pythonCache.properties")
        while (propEnum.hasMoreElements()) {
            def nxt = propEnum.nextElement()
            def p = new Properties()
            p.load(nxt.newReader())
            def dir = p.getProperty("modules.path", null)
            if (dir)
                registerModuleDirectory(dir)
            def temp = p.getProperty("temp.path", null)
            if (temp)
                registerTempDirectory(temp)
        }
        if (cacheTempDir==null)
            registerFallbackTemp()
        addShutdownHook {
            if (ModuleCache.instance.autoDelete)
                cacheTempDir.deleteDir()
        }
    }

    void tryInspectingPythonRuntime(String pythonCommand=null){
        if (pythonCommand == null)
            pythonCommand = System.getProperty("python.interpreter.command", "python")
        if (!alreadyTriedCommands.contains(pythonCommand)) {
            try {

                def process = pythonCommand.execute()
                process.out << "import sys\n"
                process.out << "print sys.path"
                process.outputStream.close()
                assert process.waitFor()==0
                def pathsList = Eval.me(process.text)
                pathsList.findAll().each {
                    try {
                        this.registerModuleDirectory(it)
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable t) {
                log.warn("Throwable caught while trying to use python outside this process!: $t")
            }
            alreadyTriedCommands.add(pythonCommand)
        }
    }

    void registerModuleDirectory(String path) {
        def f = new File(path)
        assert f.exists()
        moduleDirs.add(f)
        log.debug("Successfully registered directory $path")
    }

    void registerTempDirectory(String path) {
        assert cacheTempDir == null
        def f = new File(path)
        assert f.exists()
        cacheTempDir = f
    }

    void registerFallbackTemp(){
        def tempDir = new File(System.getProperty("java.io.tmpdir"))
        assert tempDir.exists()
        cacheTempDir = new File(tempDir, "iCanHasPython")
        if (!cacheTempDir.exists())
            cacheTempDir.mkdirs()
    }

    List<File> getAllModuleDirs(){
        [cacheTempDir]+moduleDirs
    }

    String getPythonPathString(){
        CodeStringUtils.instance.pythonize(allModuleDirs)
    }

    String getPathSnippet(){
        "import sys\nsys.path.extend($pythonPathString)\n"
    }

    boolean isImportable(String moduleName) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("python");
        try {
            engine.eval("${pathSnippet}import $moduleName")
            return true
        } catch (Throwable t) {
            return false
        }
    }

    protected String moduleToPath(String moduleName) {
        moduleName.split("[.]").join("/")
    }

    void fetchIfNeeded(String moduleName, URL source){
        if (!isImportable(moduleName)) {
            try {
                def target = new File(cacheTempDir, "${moduleToPath(moduleName)}.py")
                target.text = source.text
                log.debug "Successfully fetched module $moduleName"
            } catch (Throwable t) {
                throw new FetchingException("Something went wrong while fetching module $moduleName!", t)
            }
        }
    }

    @InheritConstructors
    @Canonical
    static class FetchingException extends RuntimeException {}
}
