package currency

import java.util.{Currency, Locale}

class MoneyAmount(val fullAmountInCents: Long, val currency: Currency) {
  val units: Long = -1 // TODO: Write tests for this
  val subdivisions: Short = -1 // TODO: Write tests for this

}
