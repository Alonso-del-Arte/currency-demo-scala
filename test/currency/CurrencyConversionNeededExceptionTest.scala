package currency

import java.util.Currency

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

import scala.util.Random
class CurrencyConversionNeededExceptionTest {

  @Test def testDefaultMessage(): Unit = {
    val currencyA = CurrencyChooser.chooseCurrency
    val amountA: MoneyAmount = new MoneyAmount(Random.nextInt, currencyA)
    val currencyB = CurrencyChooser.chooseCurrencyOtherThan(currencyA)
    val amountB: MoneyAmount = new MoneyAmount(Random.nextInt, currencyB)
    val instance = new CurrencyConversionNeededException(amountA, amountB)
    val expected = "Currency conversion needed for the two amounts"
    val actual = instance.getMessage
    assertEquals(expected, actual)
  }

}
