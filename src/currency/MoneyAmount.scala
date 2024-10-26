package currency

import java.util.{Currency, Locale}

object MoneyAmount {

  // TODO: Write tests for this
  private[currency] def unitsDivisor(currency: Currency): Int = {
    var divisor = 1
    val stop = currency.getDefaultFractionDigits
    var index = 0;
    while (index < stop) {
      divisor *= 10
      index += 1
    }
    divisor
  }

}

class MoneyAmount(val fullAmountInCents: Long, val currency: Currency) {
  val units: Long = -1 // TODO: Write tests for this
  val subdivisions: Short = -1 // TODO: Write tests for this

  override def toString: String = {
    val symbol = this.currency.getSymbol
    val numStr = this.fullAmountInCents.toString
    val len = numStr.length
    val fractionDigits = this.currency.getDefaultFractionDigits
    if (fractionDigits == 0) {
      s"$symbol${this.fullAmountInCents}"
    } else {
      val dot = len - fractionDigits
      s"$symbol${numStr.substring(0, dot)}.${numStr.substring(dot, len)}"
    }
  }

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
