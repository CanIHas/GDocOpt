package can.i.has.docopt

import groovy.util.logging.Commons

import static can.i.has.docopt.Docopt.docopt
import javax.script.ScriptException as DocoptException

@Commons("logger")
class DocoptTest extends GroovyTestCase {

    /**
     def test_commands():
         assert docopt('Usage: prog add', 'add') == {'add': True}
         assert docopt('Usage: prog [add]', '') == {'add': False}
         assert docopt('Usage: prog [add]', 'add') == {'add': True}
         assert docopt('Usage: prog (add|rm)', 'add') == {'add': True, 'rm': False}
         assert docopt('Usage: prog (add|rm)', 'rm') == {'add': False, 'rm': True}
         assert docopt('Usage: prog a b', 'a b') == {'a': True, 'b': True}
         with raises(DocoptExit):
            docopt('Usage: prog a b', 'b a')
     */
    void testCommands() {
        assert docopt('Usage: prog add', ['add']) == ['add': true]
        assert docopt('Usage: prog [add]', []) == ['add': false]
        assert docopt('Usage: prog [add]', ['add']) == ['add': true]
        assert docopt('Usage: prog (add|rm)', ['add']) == ['add': true, 'rm': false]
        assert docopt('Usage: prog (add|rm)', ['rm']) == ['add': false, 'rm': true]
        assert docopt('Usage: prog a b', ['a', 'b']) == ['a': true, 'b': true]
        shouldFail(DocoptException, {docopt('Usage: prog a b', ['b', 'a'])})
    }
    /**
     def test_long_options_error_handling():
         #    with raises(DocoptLanguageError):
         #        docopt('Usage: prog --non-existent', '--non-existent')
         #    with raises(DocoptLanguageError):
         #        docopt('Usage: prog --non-existent')
         with raises(DocoptExit):
            docopt('Usage: prog', '--non-existent')
         with raises(DocoptExit):
            docopt('Usage: prog [--version --verbose]\n'
         'Options: --version\n --verbose', '--ver')
         with raises(DocoptLanguageError):
            docopt('Usage: prog --long\nOptions: --long ARG')
         with raises(DocoptExit):
            docopt('Usage: prog --long ARG\nOptions: --long ARG', '--long')
         with raises(DocoptLanguageError):
            docopt('Usage: prog --long=ARG\nOptions: --long')
         with raises(DocoptExit):
            docopt('Usage: prog --long\nOptions: --long', '--long=ARG')
     */
    void testLongOptionsErrorHandling() {
        shouldFail(DocoptException) {
            docopt('Usage: prog', '--non-existent')
        }
        shouldFail(DocoptException) {
            docopt('Usage: prog [--version --verbose]\n' +
        'Options: --version\n --verbose', '--ver')
        }
        shouldFail(DocoptException) {
            docopt('Usage: prog --long\nOptions: --long ARG')
        }

        shouldFail(DocoptException) {
            docopt('Usage: prog --long ARG\nOptions: --long ARG', '--long')
        }

        shouldFail(DocoptException) {
            docopt('Usage: prog --long=ARG\nOptions: --long')
        }

        shouldFail(DocoptException) {
            docopt('Usage: prog --long\nOptions: --long', '--long=ARG')
        }
    }

    /**
     def test_short_options_error_handling():
         with raises(DocoptLanguageError):
            docopt('Usage: prog -x\nOptions: -x  this\n -x  that')

         #    with raises(DocoptLanguageError):
         #        docopt('Usage: prog -x')
         with raises(DocoptExit):
            docopt('Usage: prog', '-x')

         with raises(DocoptLanguageError):
            docopt('Usage: prog -o\nOptions: -o ARG')
         with raises(DocoptExit):
            docopt('Usage: prog -o ARG\nOptions: -o ARG', '-o')
     */
    void test_short_options_error_handling() {
        shouldFail(DocoptException) {
            docopt('Usage: prog -x\nOptions: -x  this\n -x  that')
        }

        shouldFail(DocoptException) {
            docopt('Usage: prog', '-x')
        }

        shouldFail(DocoptException) {
            docopt('Usage: prog -o\nOptions: -o ARG')
        }
        shouldFail(DocoptException) {
            docopt('Usage: prog -o ARG\nOptions: -o ARG', '-o')
        }
    }

    /**
    def test_matching_paren():
         with raises(DocoptLanguageError):
            docopt('Usage: prog [a [b]')
         with raises(DocoptLanguageError):
             docopt('Usage: prog [a [b] ] c )')
    */
    void test_matching_paren() {
        shouldFail(DocoptException) {
            docopt('Usage: prog [a [b]')
        }
        shouldFail(DocoptException) {
            docopt('Usage: prog [a [b] ] c )')
        }
    }

    /**
     def test_allow_double_dash():
         assert docopt('usage: prog [-o] [--] <arg>\nkptions: -o',
                                '-- -o') == {'-o': False, '<arg>': '-o', '--': True}
         assert docopt('usage: prog [-o] [--] <arg>\nkptions: -o',
                                '-o 1') == {'-o': True, '<arg>': '1', '--': False}
         with raises(DocoptExit):  # "--" is not allowed; FIXME?
            docopt('usage: prog [-o] <arg>\noptions:-o', '-- -o')
     */
    void test_allow_double_dash(){
        assert docopt('usage: prog [-o] [--] <arg>\nkptions: -o',
                            ['--', '-o']) == ['-o': false, '<arg>': '-o', '--': true]
        assert docopt('usage: prog [-o] [--] <arg>\nkptions: -o',
                            ['-o', '1']) == ['-o': true, '<arg>': '1', '--': false]
        shouldFail(DocoptException) {
            docopt('usage: prog [-o] <arg>\noptions:-o', ['--', '-o'])
        }
    }


    /**
     def test_docopt():
         doc = '''Usage: prog [-v] A

         Options: -v  Be verbose.'''
         assert docopt(doc, 'arg') == {'-v': False, 'A': 'arg'}
         assert docopt(doc, '-v arg') == {'-v': True, 'A': 'arg'}

         doc = """Usage: prog [-vqr] [FILE]
         prog INPUT OUTPUT
         prog --help

         Options:
         -v  print status messages
         -q  report only file names
         -r  show all occurrences of the same error
         --help

         """
         a = docopt(doc, '-v file.py')
         assert a == {'-v': True, '-q': False, '-r': False, '--help': False,
                    'FILE': 'file.py', 'INPUT': None, 'OUTPUT': None}

         a = docopt(doc, '-v')
         assert a == {'-v': True, '-q': False, '-r': False, '--help': False,
                    'FILE': None, 'INPUT': None, 'OUTPUT': None}

         with raises(DocoptExit):  # does not match
            docopt(doc, '-v input.py output.py')

         with raises(DocoptExit):
            docopt(doc, '--fake')

         with raises(SystemExit):
            docopt(doc, '--hel')

         #with raises(SystemExit):
         #    docopt(doc, 'help')  XXX Maybe help command?
     */
    void test_docopt() {
        def doc = '''Usage: prog [-v] A

         Options: -v  Be verbose.'''
        assert docopt(doc, ['arg']) == ['-v': false, 'A': 'arg']
        assert docopt(doc, ['-v', 'arg']) == ['-v': true, 'A': 'arg']

        doc = """Usage: prog [-vqr] [FILE]
         prog INPUT OUTPUT
         prog --help

         Options:
         -v  print status messages
         -q  report only file names
         -r  show all occurrences of the same error
         --help

         """
        def a = docopt(doc, ['-v', 'file.py'])
        assert a==['-v': true, '-q': false, '-r': false, '--help': false,
            'FILE': 'file.py', 'INPUT': null, 'OUTPUT': null]

        a = docopt(doc, ['-v'])
        assert a == ['-v': true, '-q': false, '-r': false, '--help': false,
            'FILE': null, 'INPUT': null, 'OUTPUT': null]

        shouldFail(DocoptException) {
            docopt(doc, ['-v', 'input.py', 'output.py'])
        }
        shouldFail(DocoptException) {
            docopt(doc, ['--fake'])
        }
        shouldFail(DocoptException) {
            docopt(doc, ['--hel'])
        }
    }

}
