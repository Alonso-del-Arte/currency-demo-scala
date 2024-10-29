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
    EURO_REPLACED_CURRENCIES ++ OTHER_EXCLUDED_CURRENCIES ++ PSEUDOCURRENCIES

  private val CURRENCIES: Set[Currency] = ALL_JRE_RECOGNIZED_CURRENCIES --
    AGGREGATE_EXCLUSIONS

  private val TOTAL_NUMBER_OF_CURRENCIES: Int = CURRENCIES.size

  private val NUMBER_OF_CALLS_MULTIPLIER_FOR_EXCLUSION_SEARCH: Int = 4

  private val NUMBER_OF_CALLS_FOR_EXCLUSION_SEARCH: Int =
    NUMBER_OF_CALLS_MULTIPLIER_FOR_EXCLUSION_SEARCH * TOTAL_NUMBER_OF_CURRENCIES

  private def examplePredicate(currency: Currency): Boolean = {
    currency.getDefaultFractionDigits > -1 && currency.getNumericCode % 2 == 1
  }

}

class CurrencyChooserTest {

  @Test def testChooseCurrencyDoesNotGivePseudocurrencies(): Unit = {
    val msgPart = " should not be pseudocurrency"
    for (_ <- 1 to CurrencyChooserTest.NUMBER_OF_CALLS_FOR_EXCLUSION_SEARCH) {
      val currency = CurrencyChooser.chooseCurrency
      val msg =
        s"${currency.getDisplayName} (${currency.getCurrencyCode}) $msgPart"
      assert(!CurrencyChooserTest.PSEUDOCURRENCIES.contains(currency), msg)
    }
  }

  @Test def testChooseCurrencyDoesNotGiveHistoricalCurrencies(): Unit = {
    val msgPart = " should not be historical currency"
    for (_ <- 1 to CurrencyChooserTest.NUMBER_OF_CALLS_FOR_EXCLUSION_SEARCH) {
      val currency = CurrencyChooser.chooseCurrency
      val msg =
        s"${currency.getDisplayName} (${currency.getCurrencyCode}) $msgPart"
      assert(!CurrencyChooserTest.HISTORICAL_CURRENCIES.contains(currency), msg)
    }
  }

  @Test def testChooseCurrencyDoesNotGiveEuroReplacedCurrencies(): Unit = {
    val msgPart = " should not be euro-replaced currency"
    for (_ <- 1 to CurrencyChooserTest.NUMBER_OF_CALLS_FOR_EXCLUSION_SEARCH) {
      val currency = CurrencyChooser.chooseCurrency
      val msg =
        s"${currency.getDisplayName} (${currency.getCurrencyCode}) $msgPart"
      assert(!CurrencyChooserTest.EURO_REPLACED_CURRENCIES.contains(currency),
        msg)
    }
  }

  @Test def testChooseCurrencyDoesNotGiveOtherExcludedCurrencies(): Unit = {
    val msgPart = " should not be a currency excluded for some other reason"
    for (_ <- 1 to CurrencyChooserTest.NUMBER_OF_CALLS_FOR_EXCLUSION_SEARCH) {
      val currency = CurrencyChooser.chooseCurrency
      val msg =
        s"${currency.getDisplayName} (${currency.getCurrencyCode}) $msgPart"
      assert(!CurrencyChooserTest.OTHER_EXCLUDED_CURRENCIES.contains(currency),
        msg)
    }
  }

  @Test def testChooseCurrency(): Unit = {
    println("chooseCurrency")
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
      val minimum = 3 * numberOfCalls / 5 - 1
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

  @org.junit.jupiter.api.Disabled @Test def testChooseCurrencyByPredicate(): Unit = {
    val actual: Currency = CurrencyChooser.chooseCurrency(examplePredicate _)
    val msg =
      s"${actual.getDisplayName} (${actual.getNumericCode}) has odd code, " +
        s"nonnegative fraction digits ${actual.getDefaultFractionDigits}"
    assert(examplePredicate(actual), msg)
  }

  @Test def testChooseCurrencyOtherThan(): Unit = {
    //
    fail("RESUME WORKING HERE")
  }

}
