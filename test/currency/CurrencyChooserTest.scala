package currency

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

  // FIXME: Type mismatch commented out, found Set[Currency] but was Currency
//  private val CURRENCIES: Set[Currency] = ALL_JRE_RECOGNIZED_CURRENCIES -
//    AGGREGATE_EXCLUSIONS

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
    var currencies: Set[Currency] = Set()
    for (_ <- 1 to numberOfCalls) {
      currencies += CurrencyChooser.chooseCurrency
    }
    val minimum = 3 * numberOfCalls / 5
    val actual = currencies.size
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

}
