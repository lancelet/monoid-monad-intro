package intro

import cats._
import cats.implicits._

import scala.util.Try

object MonadErrors extends App {

  /*
   * Monads (for handling errors)
   */

  /*
   * Imagine we want to represent success or failure of an operation...
   *
   * Success is represented by Right(value), while failure is represented by Left(error)
   */
  final case class Error(msg: String)
  type Result[A] = Either[Error, A]


  /*
   * An example of an operation that can fail might be parsing integers from strings:
   */
  def parseInt(s: String): Result[Int] =
    Try(s.toInt)
      .toEither
      .left
      .map((t: Throwable) => Error(t.getMessage))

  print("Trying to parse \"foo\" as an Int: ")
  println(parseInt("foo"))

  /*
   * Another (slightly artificial) example might be division; we can divide provided the denominator
   * is not zero:
   *
   *  a / b
   */
  def divInt(a: Int, b: Int): Result[Int] =
    if (b == 0)
      Left(Error("Divide by zero!"))
    else
      Right(a / b)

  print("Trying to divide by zero: ")
  println(divInt(4, 0))
  
  /*
   * Suppose we take two integers (expressed as Strings) and want to divide them. Three things
   * could go wrong:
   *   - the first string may not be an integer
   *   - the second string may not be an integer
   *   - the second integer may be zero
   *
   * Imperatively, these kinds of errors often end up captured by branching conditionals:
   * (This is meant to look like a lot of code in Go, C and Python)
   */
  def divStringsImperative(a: String, b: String): Result[Int] = {
    val ae = parseInt(a)
    if (ae.isLeft) {
      // in this case, we don't *need* to rebuild the Result because it's already a Result[Int],
      // but in general, we'd have to rebuild it if it were a Result[T]:
      return Left(ae.left.get)
    } else {
      val be = parseInt(b)
      if (be.isLeft) {
        return Left(be.left.get)
      } else {
        val ai = ae.right.get
        val bi = be.right.get
        divInt(ai, bi)
      }
    }
  }

  println("== divStringsImperative examples:")
  println(divStringsImperative("10", "2"))
  println(divStringsImperative("5", "0"))
  println(divStringsImperative("2", "foo"))

  /*
   * ^ The control flow branches above tend to hide the *meaning* of the code in the error handling
   *   noise. This is a very simple example, but it can get much worse.
   *
   *   This leads programmers in many languages to do things like:
   *     - ignore some kinds of error
   *     - try to use exceptions, which introduces non-local error handling, requiring non-local
   *       reasoning around control flow
   *
   *   In general, this is a bad approach.
   */

  /*
   * Monadic control flow for error handling looks much better:
   *
   * We get short-circuiting error handling "for free"!
   *
   * Essentially, we trade off a slightly more difficult abstraction for clearer operations.
   */
  def divStringsMonadic(a: String, b: String): Result[Int] = for {
    ai <- parseInt(a)
    bi <- parseInt(b)
    r  <- divInt(ai, bi)
  } yield r

  println("== divStringsMonadic examples:")
  println(divStringsMonadic("10", "2"))
  println(divStringsMonadic("5", "0"))
  println(divStringsMonadic("2", "foo"))

  /*
   * We can also do more interesting things with error handling.
   *
   * For example: imagine we have a sequence of Strings that we want to parse as Ints and multiply
   * them all. In this case, if any of the strings fail to parse, we should fail the operation.
   *
   * So, for parsing the ints we have a:
   *    String -> Result[Int]
   * and using map, we can turn this into:
   *    List[String] -> List[Result[Int]]
   *
   * However, instead of
   *    List[Result[Int]]
   * what we really want is the list of the Ints. We'd instead like to have a type like this:
   *    Result[List[Int]]
   *
   * Because Result and List are both monads, we can perform such a transformation using cats and
   * sequenceU. Putting this together produces:
   */
  import Monoids.intMultiplicationMonoidInstance   // for the multiplication monoid instance
  def mulInts(ss: List[String]): Result[Int] =
    ss.map(parseInt)
      .sequenceU
      .map(Monoid[Int].combineAll)


  println("== mulInts example:")
  println(mulInts(List("1", "2", "3")))
  println(mulInts(List("1", "foo", "3")))

  /*
   * Monads provide lots of other utility.
   *
   * They're just one of many related useful classes. Also useful to investigate early are:
   *   - Functors
   *   - Applicative Functors
   * (All monads are also applicative functors.)
   */
}
