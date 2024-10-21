package currency

import java.util.{ArrayList, Arrays, Currency, Random}
import java.util.function.Predicate

object CurrencyChooser {

  // TODO: Write tests for this
  def getSuitableCurrencies: Set[Currency] = Set(Currency.getInstance("XTS"))

  // TODO: Write tests for this
  def choosePseudoCurrency: Currency = Currency.getInstance("USD")

  // TODO: Write tests for this
  def chooseCurrency: Currency = Currency.getInstance("XTS")

  // TODO: Write tests for this
  def chooseCurrency(fractionDigits: Int): Currency =
    Currency.getInstance("JPY")

  // TODO: Write tests for this
  def chooseCurrency(predicate: Currency => Boolean): Currency =
    Currency.getInstance("XTS")

  // TODO: Write tests for this
  def chooseCurrencyOtherThan(currency: Currency): Currency =
    Currency.getInstance("XTS")

}
