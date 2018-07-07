package co.lnic.dsw

import org.scalatest._

package object stories {

  abstract class Specification extends FeatureSpec
    with GivenWhenThen
    with Matchers
    with OptionValues
    with Inside
    with Inspectors
}
