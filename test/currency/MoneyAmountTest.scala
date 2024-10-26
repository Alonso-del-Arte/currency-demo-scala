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
    val fractDigits = currency.getDefaultFractionDigits
    val fullAmountInCents = Random.nextInt(Short.MaxValue) + Byte.MaxValue
    val amount = new MoneyAmount(fullAmountInCents, currency)
    val symbol = currency.getSymbol
    val numStr = Integer.toString(fullAmountInCents)
    val len = numStr.length
    val dot = len - fractDigits
    val expected =
      s"$symbol${numStr.substring(0, dot)}.${numStr.substring(dot, len)}"
    val actual = amount.toString
    assertEquals(expected, actual)
  }

}
