package currency

import java.util.{Currency, NoSuchElementException}
import java.util.function.Predicate

import scala.collection.JavaConverters._
import scala.util.Random

object CurrencyChooser {

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

  private val CURRENCIES_ARRAY: Array[Currency] = CURRENCIES.toArray

  private val TOTAL_NUMBER_OF_AVAILABLE_CURRENCIES: Int = CURRENCIES_ARRAY.length

  // TODO: Write tests for this
  def getSuitableCurrencies: Set[Currency] = Set(Currency.getInstance("XTS"))

  // TODO: Write tests for this
  def choosePseudoCurrency: Currency = Currency.getInstance("USD")

  /**
   * Chooses a currency, preferably one suitable for an online currency
   * conversion API. Historical currencies (including euro-replaced currencies)
   * and pseudocurrencies, like gold (XAU), are excluded.
   * @return A currency. For example, Icelandic kr&oacute;na (ISK).
   */
  def chooseCurrency: Currency = {
    val index = Random.nextInt(TOTAL_NUMBER_OF_AVAILABLE_CURRENCIES)
    CURRENCIES_ARRAY(index)
  }

  // TODO: Write tests for this
  def chooseCurrency(predicate: Currency => Boolean): Currency =
    Currency.getInstance("XTS")

  // TODO: Write tests for this
  def chooseCurrency(fractionDigits: Int): Currency = {
    if (fractionDigits < 0) {
      val excMsg = s"$fractionDigits not valid"
      throw new NoSuchElementException(excMsg)
    }
    Currency.getInstance("CLF")
  }

  // TODO: Write tests for this
  def chooseCurrencyOtherThan(currency: Currency): Currency =
    Currency.getInstance("XTS")

}
