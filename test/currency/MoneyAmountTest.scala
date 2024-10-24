package currency

import java.util.{Currency, Locale}

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

object MoneyAmountTest {

  private val DOLLARS: Currency = Currency.getInstance(Locale.US)

  private val EUROS: Currency = Currency.getInstance("EUR")

  private val YEN: Currency = Currency.getInstance(Locale.JAPAN)

}

class MoneyAmountTest {

}
