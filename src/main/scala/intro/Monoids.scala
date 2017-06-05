package intro

import cats._

object Monoids extends App {

  /*
   * A "Monoid" is something from abstract algebra that is used commonly in functional programming.
   * It's essentially a "design pattern". It's useful because it:
   *   - defines an interface for some very common operations (like a Java interface),
   *   - *should* always obey some simple laws, which help to guarantee its behaviour
   */

  /*
   * Definitions:
   *
   * A Magma is:
   *   - a type
   *   - with a binary operation (written here as the dot .): eg:
   *           a . b = c,
   *             (a, b and c are of the *same* type)
   *      - this property is called "closure": the binary operation produces another element of the
   *        *same type*. Anything satisfying this is a magma.
   * 
   * A Semigroup:
   *   - is a magma
   *   - with an *associative* binary operation; ie: (a . b) . c = a . (b . c)
   *
   * A Monoid:
   *   - is a semigroup
   *   - with an identity (/ "empty") element, I: I . a = a . I = a
   *
   * So there's a heirarchy:
   *   - Magma:     has a binary operation
   *   - Semigroup: ... with associativity
   *   - Monoid:    ... with an identity element
   * All monoids are semigroups. All semigroups are magmas.
   *
   * There are other common variations too. eg. a "commutative monoid" also satisfies:
   *    a . b = b . a
   */

  // Example of a monoid: Strings!
  //
  // NOTE: this is a monoid, but not a commutative monoid:
  //         "Hello" + "World" != "World" + "Hello"
  implicit val stringMonoidInstance: Monoid[String] = new Monoid[String] {
    override def empty: String = ""
    override def combine(a: String, b: String): String = a + b
  }

  // Another example of a monoid: Integers with multiplication
  //  (Integers can also be a monoid with addition as the operation, but then the empty
  //   element is 0 instead of 1.)
  //
  // NOTE: this *IS* a commutative monoid:
  //          1 * 2 == 2 * 1
  implicit val intMultiplicationMonoidInstance: Monoid[Int] = new Monoid[Int] {
    override def empty: Int = 1
    override def combine(a: Int, b: Int): Int = a * b
  }

  /*
   * So, we could for example concatenate strings, or multiply integers using their own operations:
   */
  val strs: List[String] = List("Hello", " ", "World")
  val ints: List[Int]    = List(1, 2, 3)

  val concatStr: String = strs.reduce(_ + _)
  val mulInts: Int      = ints.reduce(_ * _)
  println(s"concatStr = ${concatStr}")
  println(s"mulInts   = ${mulInts}")

  /*
   * However, a Monoid provides a way to do this kind of operation in a completely uniform way.
   * 
   * (And we know that we're dealing with something that should satisfy the monoid *laws* because
   * that's how it's been declared. Satisfying the laws is critically important, and there are
   * tests in cats that can easily check this for a particular type.)
   */
  val concatStrMonoid: String = Monoid[String].combineAll(strs)
  val mulIntsMonoid: Int      = Monoid[Int].combineAll(ints)
  println(s"concatStrMonoid = ${concatStrMonoid}")
  println(s"mulIntsMonoid   = ${mulIntsMonoid}")

  /*
   * Monoid will also work with an empty list, because it has an identity / empty element:
   */
  val emptyString: String = Monoid[String].combineAll(List.empty)
  println("emptyString = \"" + s"$emptyString" + "\"")

  /*
   * In summary, a Monoid:
   *   - provides a uniform interface for binary operations on things (like a design pattern)
   *   - this is often used for some notion of combining or addition of types
   *   - satisfies the closure, associativity and identity laws:
   *        a . b = c, where a, b and c are all of the same type  (closure)
   *        (a . b) . c = a . (b . c)                             (associativity)
   *        I . a = a . I                                         (identity)
   *
   * Many other things can also be represented as monoids:
   *   - configuration details (where later configurations override earlier ones)
   *   - diagrams (diagrams layered on top of each other)
   *   - colours in RGB under addition (with black as identity)
   *   - etc.
   */

}
