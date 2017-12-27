import re
import sys

def type2text(typename):
    if typename in ('int', 'byte', 'float', 'double'):
        text = '0'
    elif typename == 'char':
        text = "'0'"
    elif typename == 'boolean':
        text = 'true'
    elif typename == 'String':
        text = '""'
    elif typename == 'void':
        text = ''
    else:
        text = f'new {typename.name}('
        for init in typename.inits:
            text += f'{typename(init)}, '
        text += ')'
    return text

class JavaObject(object):
    name = None
    static = '' # Lalallal
    region = 'public'

class JavaAttr(JavaObject):
    def __init__(self, name, typename, static=False):
        self.name = name
        self.type = typename
        self.static = 'static ' if static else ''
        self.value = None

class JavaMehotd(JavaObject):
    def __init__(self, name, typename, static=False):
        self.name = name
        self.returntype = typename
        self.static = 'static ' if static else ''
        self.arg_types = []

class JavaClass(object):

    def __init__(self, name, pkname):
        self.name = name
        self.pkname = pkname
        self.attrs = []
        self.inits = []
        self.methods = []
        self.imports = []

    def __str__(self):
        return self.name

    def __repr__(self):
        return self.name

    def addAttr(self):
        pass

    def addMethod(self):
        pass

    def submit(self):
        code = f'package {self.pkname};\n\n'
        for pkname in self.imports:
            code += f'import {pkname};\n'
        code += f'\npublic class {self.name} {{\n\n'
        for attr in self.attrs:
            code += f'\tpublic {attr.static}{attr.type} {attr.name};\n'
        code += '\n'
        # code += f'\tpublic {self.name}({type2text(self.inits)}) {{\n'
        for method in self.methods:
            code += f'\tpublic {method.static}{method.returntype} {method.name}('
            i = 0
            for arg in method.arg_types:
                i += 1
                if i >= len(method.arg_types):
                    code += f'{arg} p{i}'
                else:
                    code += f'{arg} p{i}, '
            code += ') { '
            code += f'return {type2text(method.returntype)}; '
            code += '}\n'
        code += '}'
        return code

def remove_comment(source):
    result = re.sub(r'//.*', '', source)
    result = re.sub(r'/\*.*\*/', '', source)
    return result

def get_packagename(source):
    m = re.match(r'[\s\n]*package (.*);', source)
    if m:
        return m.group(1)
    raise Exception('Not Founght PackageName!')

def get_imports(source):
    import_pknames = re.findall(r'import (.*);', source)
    return import_pknames

def main():
    fname = sys.argv[1]
    source = open(fname).read()
    source = remove_comment(source)
    source_pkname = get_packagename(source)
    import_pknames = get_imports(source)

if __name__ == '__main__':
    main()
