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
    else:
        text = f'new {typename.name}({type2text(typename.initargs})'
    return text


class JavaAttr(object):
    def __init__(self, name, typename, static=False):
        self.name = name
        self.type = typename
        self.static = 'static' if static else ''

class JavaClass(object):

    def __init__(self, name, pkname):
        self.name = name
        self.pkname = pkname
        self.attrs = []
        self.methods = []
        self.imports = []
        self.initargs = []

    def submit(self):
        code  = f'package {self.pkname};\n\n'
        for pkname in self.imports:
            code += f'import {pkname};\n'
        code += f'\npublic class {self.name} {{\n\n'
        for attr in self.attrs:
            code += f'\tpublic {attr.static} {attr.type} {attr.name};\n'
        code += '\n'
        code += f'\tpublic {self.name}({type2text(self.initargs)}) {{\n'
        for method in self.methods:
            code += f'\tpublic {attr.static} {attr.type} {attr.name} {{ '
            code += f'return {text}; '
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
