package currency

@SerialVersionUID(4559181705386131456L)
class CurrencyConversionNeededException(val amountA: MoneyAmount,
                                        val amountB: MoneyAmount,
                                        message: String =
                                        "Currency conversion needed for the two amounts")
  extends RuntimeException(message)
