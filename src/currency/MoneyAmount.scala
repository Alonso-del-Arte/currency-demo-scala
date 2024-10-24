package currency

import java.util.{Currency, Locale}

object MoneyAmount {

  // TODO: Write tests for this
  private[currency] def unitsDivisor(currency: Currency): Int = -1

}

class MoneyAmount(val fullAmountInCents: Long, val currency: Currency) {
  val units: Long = -1 // TODO: Write tests for this
  val subdivisions: Short = -1 // TODO: Write tests for this

  // TODO: Write tests for this
  def +(addend: MoneyAmount): MoneyAmount = this

  // TODO: Write tests for this
  def unary_-(): MoneyAmount = this

  // TODO: Write tests for this
  def -(subtrahend: MoneyAmount): MoneyAmount = this

  // TODO: Write tests for this
  def *(multiplicand: Int): MoneyAmount = this

  // TODO: Write tests for this
  def /(divisor: Int): MoneyAmount = this

}
