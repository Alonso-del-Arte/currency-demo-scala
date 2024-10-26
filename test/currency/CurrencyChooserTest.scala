package currency

import currency.CurrencyChooserTest.{CURRENCIES, examplePredicate}

import java.util.{Currency, NoSuchElementException}
import java.util.function.Predicate

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

import scala.collection.JavaConverters._
import scala.util.Random

object CurrencyChooserTest {

  private val ALL_JRE_RECOGNIZED_CURRENCIES: Set[Currency] =
    Currency.getAvailableCurrencies.asScala.toSet

  private val PSEUDOCURRENCIES: Set[Currency] =
    ALL_JRE_RECOGNIZED_CURRENCIES.filter(_.getDefaultFractionDigits < 0)

  private def isHistoricalCurrency(currency: Currency): Boolean = {
    val name = currency.getDisplayName
    name.contains("\u002818") || name.contains("\u002819") ||
      name.contains("\u002820")
  }

  private val HISTORICAL_CURRENCIES: Set[Currency] =
    ALL_JRE_RECOGNIZED_CURRENCIES.filter(isHistoricalCurrency)

  private val EURO_REPLACED_CURRENCY_CODES: Set[String] = Set("ADP", "ATS",
    "BEF", "CYP", "DEM", "EEK", "ESP", "FIM", "FRF", "GRD", "IEP", "ITL", "LUF",
    "MTL", "NLG", "PTE", "SIT")

  private val EURO_REPLACED_CURRENCIES: Set[Currency] =
    EURO_REPLACED_CURRENCY_CODES.map(Currency.getInstance)

  private val OTHER_EXCLUSION_CODES: Set[String] = Set("AYM", "BGL", "BOV",
    "CHE", "CHW", "COU", "GWP", "MGF", "MXV", "SRG", "STN", "TPE", "USN", "USS",
    "UYI", "VED", "ZWN")

  private val OTHER_EXCLUDED_CURRENCIES: Set[Currency] =
    OTHER_EXCLUSION_CODES.map(Currency.getInstance)

  private val AGGREGATE_EXCLUSIONS: Set[Currency] = HISTORICAL_CURRENCIES ++
    EURO_REPLACED_CURRENCIES ++ OTHER_EXCLUDED_CURRENCIES

  private val CURRENCIES: Set[Currency] = ALL_JRE_RECOGNIZED_CURRENCIES --
    AGGREGATE_EXCLUSIONS

  private def examplePredicate(currency: Currency): Boolean = {
    currency.getDefaultFractionDigits > -1 && currency.getNumericCode % 2 == 1
  }

}

class CurrencyChooserTest {

  @Test def testChooseCurrency(): Unit = {
    println("chooseCurrency")
    val cur: Currency = CurrencyChooser.chooseCurrency
    val msgPart =
      s"${cur.getDisplayName} (${cur.getCurrencyCode}) shouldn't be "
    assert(!(CurrencyChooserTest.PSEUDOCURRENCIES contains cur),
      s"""$msgPart pseudocurrency""")
    assert(!(CurrencyChooserTest.HISTORICAL_CURRENCIES contains cur),
      s"""$msgPart historical""")
    assert(!(CurrencyChooserTest.EURO_REPLACED_CURRENCIES contains cur),
      s"""$msgPart euro-replaced""")
    assert(!(CurrencyChooserTest.OTHER_EXCLUDED_CURRENCIES contains cur),
      s"""$msgPart other exclusion""")
  }

  @Test def testChooseCurrencyGivesEnoughDistinctCurrencies(): Unit = {
    val numberOfCalls = Random.nextInt(64) + 16
    var givenCurrencies: Set[Currency] = Set()
    for (_ <- 1 to numberOfCalls) {
      givenCurrencies += CurrencyChooser.chooseCurrency
    }
    val minimum = 3 * numberOfCalls / 5
    val actual = givenCurrencies.size
    val msg = s"Expected at least $minimum distinct currencies, got $actual"
    assert(actual >= minimum, msg)
  }

  @Test def testNegativeCurrencyFractionDigitsCausesException(): Unit = {
    val badFractionDigits = -Random.nextInt(Short.MaxValue) - 1
    val message = s"$badFractionDigits fraction digits should cause exception"
    val exc = assertThrows(classOf[NoSuchElementException],() => {
      val badResult = CurrencyChooser.chooseCurrency(badFractionDigits)
      println(s"$message, not given result ${badResult.getDisplayName}")
    }, message)
    val excMsg = exc.getMessage
    assert(excMsg != null, "Exception message should not be null")
    assert(!excMsg.isBlank, "Exception message should not be blank")
    println("\"" +excMsg + "\"")
  }

  @Test def testChooseCurrencyNoFractionDigits(): Unit = {
    val selectedCurrencies: Set[Currency] =
      CURRENCIES.filter(_.getDefaultFractionDigits == 0)
    val numberOfCalls = selectedCurrencies.size
    var givenCurrencies: Set[Currency] = Set()
    for (_ <- 1 to numberOfCalls) {
      val currency = CurrencyChooser.chooseCurrency(0)
      val message = s"${currency.getDisplayName} should have 0 fraction digits"
      assertEquals(0, currency.getDefaultFractionDigits, message)
      givenCurrencies += currency
    }
    val minimum = 3 * numberOfCalls / 5
    val actual = givenCurrencies.size
    val msg = s"Expected at least $minimum distinct currencies, got $actual"
    assert(actual >= minimum, msg)
  }

  @Test def testChooseCurrencyWithFractionDigitsTwoToFour(): Unit = {
    for (expected <- 2 to 4) {
      val selectedCurrencies =
        CURRENCIES.filter(_.getDefaultFractionDigits == expected)
      val numberOfCalls = selectedCurrencies.size
      var givenCurrencies: Set[Currency] = Set()
      for (_ <- 1 to numberOfCalls) {
        val currency = CurrencyChooser.chooseCurrency(expected)
        val message =
          s"${currency.getDisplayName} should have $expected fraction digits"
        assertEquals(expected, currency.getDefaultFractionDigits, message)
        givenCurrencies += currency
      }
      val minimum = 3 * numberOfCalls / 5
      val actual = givenCurrencies.size
      val msg = s"Expected at least $minimum distinct currencies, got $actual"
      assert(actual >= minimum, msg)
    }
  }

  @Test def testExcessiveCurrencyFractionDigitsCausesException(): Unit = {
    val badFractionDigits = Random.nextInt(Short.MaxValue) + 5
    val message = s"$badFractionDigits fraction digits should cause exception"
    val exc = assertThrows(classOf[NoSuchElementException], () => {
      val badResult = CurrencyChooser.chooseCurrency(badFractionDigits)
      println(s"$message, not given result ${badResult.getDisplayName}")
    }, message)
    val excMsg = exc.getMessage
    assert(excMsg != null, "Exception message should not be null")
    assert(!excMsg.isBlank, "Exception message should not be blank")
    println("\"" + excMsg + "\"")
  }

  @Test def testChooseCurrencyByPredicate(): Unit = {
    val actual: Currency = CurrencyChooser.chooseCurrency(examplePredicate _)
    val msg =
      s"${actual.getDisplayName} (${actual.getNumericCode}) has odd code, " +
        s"nonnegative fraction digits ${actual.getDefaultFractionDigits}"
    assert(examplePredicate(actual), msg)
  }

  @Test def testChooseCurrencyOtherThan(): Unit = {
    val currencyArray = CurrencyChooserTest.CURRENCIES.toArray
    val length = currencyArray.length
    val currency = currencyArray(Random.nextInt(length))
    val actual = CurrencyChooser.chooseCurrencyOtherThan(currency)
    val message = s"Currency ${actual.getDisplayName} should not be the same as ${currency.getDisplayName}"
    assert(!actual.equals(currency), message)
  }

}
