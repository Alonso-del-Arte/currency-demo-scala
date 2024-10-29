package currency

@SerialVersionUID(4559181705386131456L)
class CurrencyConversionNeededException(message: String = "",
                                        val amountA: MoneyAmount,
                                        val amountB: MoneyAmount)
  extends RuntimeException
