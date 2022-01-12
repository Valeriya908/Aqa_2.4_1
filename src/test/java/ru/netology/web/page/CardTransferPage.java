package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CardTransferPage {
    private SelenideElement subheading = $("h1");
    private SelenideElement amountField = $("[data-test-id='amount'] input");
    private SelenideElement fromField = $("[data-test-id='from'] input");
    private SelenideElement buttonTopUpBalance = $("[data-test-id='action-transfer']");
    private SelenideElement errorMessage = $("[data-test-id='error-notification'] .notification__content");

    public CardTransferPage() {
        subheading.shouldBe(visible).shouldHave(text("Пополнение карты"));
    }

    public DashboardPage validMoneyTransfer(String amountTransfer, DataHelper.CardInfo cardInfo) {
        amountField.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
        amountField.setValue(amountTransfer);
        fromField.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
        fromField.setValue(cardInfo.getNumber());
        buttonTopUpBalance.click();
        return new DashboardPage();
    }

    public void invalidMoneyTransfer(String amountTransfer) {
        amountField.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
        amountField.setValue(amountTransfer);
        fromField.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
        buttonTopUpBalance.click();
    }

    public void errorMessage() {
        errorMessage.shouldBe(visible).shouldHave(text("Ошибка! Произошла ошибка"), Duration.ofSeconds(10));
    }
}



