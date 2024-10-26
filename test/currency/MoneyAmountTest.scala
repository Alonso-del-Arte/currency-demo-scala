package currency

import currency.MoneyAmountTest.DOLLARS

import java.util.{Currency, Locale}

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

import scala.util.Random

object MoneyAmountTest {

  private val DOLLARS: Currency = Currency.getInstance(Locale.US)

  private val EUROS: Currency = Currency.getInstance("EUR")

  private val YEN: Currency = Currency.getInstance(Locale.JAPAN)

}

class MoneyAmountTest {

  @Test def testToString(): Unit = {
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, MoneyAmountTest.DOLLARS)
    val symbol = MoneyAmountTest.DOLLARS.getSymbol
    val numStr = Integer.toString(fullAmountInCents)
    val len = numStr.length
    val dot = len - 2
    val expected =
      s"$symbol${numStr.substring(0, dot)}.${numStr.substring(dot, len)}"
    val actual = amount.toString
    assertEquals(expected, actual)
  }

  @Test def testToStringOtherCurrencyWith100Cents(): Unit = {
    val currency: Currency = CurrencyChooser.chooseCurrency((cur: Currency) =>
      cur.getDefaultFractionDigits == 2 && !cur.equals(DOLLARS) &&
        !cur.getSymbol.equals(cur.getCurrencyCode))
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, currency)
    val symbol = currency.getSymbol
    val numStr = Integer.toString(fullAmountInCents)
    val len = numStr.length
    val dot = len - 2
    val expected =
      s"$symbol${numStr.substring(0, dot)}.${numStr.substring(dot, len)}"
    val actual = amount.toString
    assertEquals(expected, actual)
  }

  @Test def testToStringCurrencyWithMoreThan100Subunits(): Unit = {
    val currency = CurrencyChooser.chooseCurrency(cur =>
      cur.getDefaultFractionDigits > 2)
    val fractionDigits = currency.getDefaultFractionDigits
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, currency)
    val symbol = currency.getSymbol
    val numStr = Integer.toString(fullAmountInCents)
    val len = numStr.length
    val dot = len - fractionDigits
    val expected =
      s"$symbol${numStr.substring(0, dot)}.${numStr.substring(dot, len)}"
    val actual = amount.toString
    assertEquals(expected, actual)
  }

  @Test def testToStringNoSubunits(): Unit = {
    val currency = CurrencyChooser.chooseCurrency(0)
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, currency)
    val symbol = currency.getSymbol
    val expected = s"$symbol$fullAmountInCents"
    val actual = amount.toString
    assertEquals(expected, actual)
  }

  @Test def testUnitsDivisorWhenNoSubunits(): Unit = {
    val currency = CurrencyChooser.chooseCurrency(0)
    val expected = 1
    val actual = MoneyAmount.unitsDivisor(currency)
    val message = s"Units divisor for ${currency.getDisplayName} " +
      s"(${currency.getCurrencyCode}) should be $expected"
    assertEquals(expected, actual, message)
  }

  @Test def testUnitsDivisor(): Unit = {
    println("unitsDivisor")
    var expected = 10
    for (fractionDigits <- 2 to 4) {
      expected *= 10
      val currency = CurrencyChooser.chooseCurrency(fractionDigits)
      val actual = MoneyAmount.unitsDivisor(currency)
      val message = s"Expecting $expected fraction digits for " +
        s"${currency.getDisplayName} (${currency.getCurrencyCode})"
      assertEquals(expected, actual, message)
    }
  }

  @Test def testUnitsNoSubunits(): Unit = {
    val currency = CurrencyChooser.chooseCurrency(0)
    val expected = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(expected, currency)
    val actual = amount.units
    val message = s"Units for ${currency.getDisplayName} " +
      s"(${currency.getCurrencyCode}) should be $expected"
    assertEquals(expected, actual, message)
  }

  @Test def testUnits(): Unit = {
    println("units")
    val currency = CurrencyChooser.chooseCurrency(cur =>
      cur.getDefaultFractionDigits > 0)
    val fractionDigits = currency.getDefaultFractionDigits
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, currency)
    var index = 0
    var unitsDiv = 1
    while (index < fractionDigits) {
      unitsDiv *= 10
      index += 1
    }
    val expected = fullAmountInCents / unitsDiv
    val actual = amount.units
    val message = s"Units for ${amount.toString} should be $expected"
    assertEquals(expected, actual, message)
  }

}
