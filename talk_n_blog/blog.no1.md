# How Scala is hacked over JVM? Part 1: default parameters

Scala as a language provides much more features than Java (even Java 8) I was always interested how Martin Odersky and the rest of the [gang](https://github.com/scala/scala/graphs/contributors) was able to implement features like default parameters or types over limited set instructions of `JVM`'s bytecode.
In series of short blogposts I will try to explain how some of the Scala features are implemented over JVM. 

All examples (including this text) can be found on my [github](https://github.com/romanowski/scala-bcode-cutter-link/talk_n_blog). All examples was compiled with Scala 2.12.4 over Java 8 (`1.8.0_121`).

## Default parameters

Whenever I ask anyone `Do you know how default parameters are compiled?` usual answer is `Scala is generating different versions of given method, or something?`.
This seems to be simple and elegant solution that should also work in Java well. I think it time to look at bytecode.

*You can skip bytecode part and go directly to [summary](#Summary).*


```scala
package pck

class Default {
  def foo(bar: Int = 123) = bar + 456
}
```

depompiled with javap using `-c` and `-p` flags produces:

```
public class pck.Default {
  public int foo(int);
    Code:
       0: iload_1
       1: bipush        123
       3: iadd
       4: ireturn

  public int foo$default$1();
    Code:
       0: bipush        123
       2: ireturn

  public pck.Default();
    Code:
       0: aload_0
       1: invokespecial #20                 // Method java/lang/Object."<init>":()V
       4: return
}
```

Hmm, there is no parameter-less version of `foo` so first guess was wrong. Let's look at definition site of method `Default.foo` maybe it will give us some pointers.

```scala
class DefaultUsed {
  private val default = new Default()

  def withDefault = default.foo()
  def withoutDefault = default.foo(789)
}

```

Methods withDefault and withoutDefault produce following bytecode:

```
  public int withDefault();
    Code:
       0: aload_0
       1: invokespecial #19                 // Method default:()Lpck/Default;
       4: aload_0
       5: invokespecial #19                 // Method default:()Lpck/Default;
       8: invokevirtual #24                 // Method pck/Default.foo$default$1:()I
      11: invokevirtual #28                 // Method pck/Default.foo:(I)I
      14: ireturn

  public int withoutDefault();
    Code:
       0: aload_0
       1: invokespecial #19                 // Method default:()Lpck/Default;
       4: sipush        789
       7: invokevirtual #28                 // Method pck/Default.foo:(I)I
      10: ireturn
```

If you compare code carefully you will see that `withDefault` instead of loading static Int value (`sipush 789` instruction in withoutDefault) it does following code:

```
 4: aload_0
 5: invokespecial #19                 // Method default:()Lpck/Default;
 8: invokevirtual #24                 // Method pck/Default.foo$default$1:()I
```
JVM byte code is stack based assembler. To invoke method JVM first store this reference and all parameters on stack then calls specific method and push returned value on top of stack. With that in mind it is (hopefully) clear that Scala is invoking a method `Default.foo$default$1` that is later passed as first parameter to `Default.foo`.

If we look (below) on `Default.foo$default$1`'s bytcode we will see that it is exactly default value for `Default.foo`
 

```
  public int foo$default$1();
    Code:
       0: bipush        123
       2: ireturn
```

How compiler knows that there are default parameters that can be used? It is all stored scala metadata information kept inside parameter for `ScalaSignature` annotation. Reading and exploring those information is however topic for another past.

## Summary

Method with default parameters has no special representation in bytecode. Instead Scala compiler generate dedicated method for each default parameters and if required apply them on use site. Hence default parameters cannot be used from Java code (you will need to provide all parameters).