package can.i.has.python


class YesYouCan {
    final static AdHocInterpreter interpreter = AdHocInterpreter.instance
    final static CodeStringUtils codeUtils = CodeStringUtils.instance
    final static ModuleCache modules = ModuleCache.instance
    final static Closure pythonize = codeUtils.&pythonize
}
