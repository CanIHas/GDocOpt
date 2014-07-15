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
    void testShortOptionsErrorHandling() {
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
    void testMatchingParen() {
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
    void testAllowDoubleDash(){
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
    void testDocopt() {
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

    /**
     def test_language_errors():
         with raises(DocoptLanguageError):
            docopt('no usage with colon here')
         with raises(DocoptLanguageError):
            docopt('usage: here \n\n and again usage: here')
     */

    void testLanguageErrors() {
         shouldFail(DocoptException) {
            docopt('no usage with colon here')
          }
         shouldFail(DocoptException) {
            docopt('usage: here \n\n and again usage: here')
         }
    }

    /**
     def test_issue_40():
         with raises(SystemExit):  # i.e. shows help
            docopt('usage: prog --help-commands | --help', '--help')
         assert docopt('usage: prog --aabb | --aa', '--aa') == {'--aabb': False,
         '--aa': True}
     */
     void testIssue40() {
         shouldFail(DocoptException) {
            docopt('usage: prog --help-commands | --help', ['--help'])
          }
         assert docopt('usage: prog --aabb | --aa', ['--aa']) == ['--aabb': false,
                        '--aa': true]
     }

    /**
     def test_count_multiple_flags():
         assert docopt('usage: prog [-v]', '-v') == {'-v': True}
         assert docopt('usage: prog [-vv]', '') == {'-v': 0}
         assert docopt('usage: prog [-vv]', '-v') == {'-v': 1}
         assert docopt('usage: prog [-vv]', '-vv') == {'-v': 2}
         with raises(DocoptExit):
            docopt('usage: prog [-vv]', '-vvv')
         assert docopt('usage: prog [-v | -vv | -vvv]', '-vvv') == {'-v': 3}
         assert docopt('usage: prog -v...', '-vvvvvv') == {'-v': 6}
         assert docopt('usage: prog [--ver --ver]', '--ver --ver') == {'--ver': 2}
     */

    void testCountMultipleFlags() {
         assert docopt('usage: prog [-v]', ['-v']) == ['-v': true]
         assert docopt('usage: prog [-vv]', []) == ['-v': 0]
         assert docopt('usage: prog [-vv]', ['-v']) == ['-v': 1]
         assert docopt('usage: prog [-vv]', ['-vv']) == ['-v': 2]
         shouldFail(DocoptException) {
            docopt('usage: prog [-vv]', ['-vvv'])
            }
         assert docopt('usage: prog [-v | -vv | -vvv]', ['-vvv']) == ['-v': 3]
         assert docopt('usage: prog -v...', ['-vvvvvv']) == ['-v': 6]
         assert docopt('usage: prog [--ver --ver]', ['--ver', '--ver']) == ['--ver': 2]
         }

    /**
     def test_any_options_parameter():
         with raises(DocoptExit):
            docopt('usage: prog [options]', '-foo --bar --spam=eggs')
         #    assert docopt('usage: prog [options]', '-foo --bar --spam=eggs',
         #                  any_options=True) == {'-f': True, '-o': 2,
         #                                         '--bar': True, '--spam': 'eggs'}
         with raises(DocoptExit):
            docopt('usage: prog [options]', '--foo --bar --bar')
         #    assert docopt('usage: prog [options]', '--foo --bar --bar',
         #                  any_options=True) == {'--foo': True, '--bar': 2}
         with raises(DocoptExit):
            docopt('usage: prog [options]', '--bar --bar --bar -ffff')
         #    assert docopt('usage: prog [options]', '--bar --bar --bar -ffff',
         #                  any_options=True) == {'--bar': 3, '-f': 4}
         with raises(DocoptExit):
            docopt('usage: prog [options]', '--long=arg --long=another')
         #    assert docopt('usage: prog [options]', '--long=arg --long=another',
         #                  any_options=True) == {'--long': ['arg', 'another']}
     */


    void testAnyOptionsParameter(){
         shouldFail(DocoptException) {
            docopt('usage: prog [options]', ['-foo', '--bar', '--spam=eggs'])
               }
         shouldFail(DocoptException) {
            docopt('usage: prog [options]', ['--foo', '--bar', '--bar'])
              }
         shouldFail(DocoptException) {
            docopt('usage: prog [options]', ['--bar', '--bar', '--bar', '-ffff'])
             }
         shouldFail(DocoptException) {
            docopt('usage: prog [options]', ['--long=arg', '--long=another'])
            }
        }

    /**
     def test_default_value_for_positional_arguments():
         doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x]
         """
         a = docopt(doc, '')
         assert a == {'--data': ['x']}
         doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x y]
         """
         a = docopt(doc, '')
         assert a == {'--data': ['x', 'y']}
         doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x y]
         """
         a = docopt(doc, '--data=this')
         assert a == {'--data': ['this']}
     */
     void testDefaultValueForPositionalArguments() {
         def doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x]
         """
         def a = docopt(doc, [])
         assert a == ['--data': ['x']]
         doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x y]
         """
         a = docopt(doc, [])
         assert a == ['--data': ['x', 'y']]
         doc = """Usage: prog [--data=<data>...]\n
         Options:\n\t-d --data=<arg>    Input data [default: x y]
         """
         a = docopt(doc, ['--data=this'])
         assert a == ['--data': ['this']]
      }


}
