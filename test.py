import javafill as jf

class_A = jf.JavaClass('A', 'com.xclz')

attr_a = jf.JavaAttr('aa', 'int')
attr_b = jf.JavaAttr('ab', 'char', True)

method_a = jf.JavaMehotd('ma', 'int')
method_b = jf.JavaMehotd('mb', 'String', True)
method_c = jf.JavaMehotd('mc', class_A)

# class_A.imports = ['a.b.H', 'c.a.N']
class_A.attrs = [attr_a, attr_b]
class_A.methods = [method_a, method_b, method_c]

print(class_A.submit())
