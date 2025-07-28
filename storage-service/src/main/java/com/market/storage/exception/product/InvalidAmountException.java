package com.market.storage.exception.product;

public class InvalidAmountException extends ProductException {
    public InvalidAmountException(int amount) {
        super("Количество товара должно быть положительным числом (запрошено " + amount + ")");
    }
}
