package ru.netology.web.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {

    int amountTransfer = 4350;
    int amountTransferOverLimit = 15000;

    @BeforeEach
    public void setUpTest() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCode(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);
    }

    @AfterEach
    public void equalizingBalance() {
        var dashboardPage = new DashboardPage();
        int currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        int currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        int amountTransfer;
        if (currentBalanceFirstCard > currentBalanceSecondCard) {
            amountTransfer = (currentBalanceFirstCard - currentBalanceSecondCard) / 2;
            var cardTransferPage = dashboardPage.secondCardTransfer();
            cardTransferPage.moneyTransfer(amountTransfer, DataHelper.getFirstCardInfo());
        }
        if (currentBalanceSecondCard > currentBalanceFirstCard) {
            amountTransfer = (currentBalanceSecondCard - currentBalanceFirstCard) / 2;
            var cardTransferPage = dashboardPage.firstCardTransfer();
            cardTransferPage.moneyTransfer(amountTransfer, DataHelper.getSecondCardInfo());
        }
    }

    @Test
    void shouldTransferMoneyFromFirstToSecond() {
        var dashboardPage = new DashboardPage();
        var currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        var currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        var cardTransferPage = dashboardPage.secondCardTransfer();
        cardTransferPage.moneyTransfer(amountTransfer, DataHelper.getFirstCardInfo());
        assertEquals(currentBalanceFirstCard - amountTransfer, dashboardPage.getCardBalance(0));
        assertEquals(currentBalanceSecondCard + amountTransfer, dashboardPage.getCardBalance(1));
    }

    @Test
    void shouldTransferMoneyFromSecondToFirst() {
        var dashboardPage = new DashboardPage();
        var currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        var currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        var cardTransferPage = dashboardPage.firstCardTransfer();
        cardTransferPage.moneyTransfer(amountTransfer, DataHelper.getSecondCardInfo());
        assertEquals(currentBalanceSecondCard - amountTransfer, dashboardPage.getCardBalance(1));
        assertEquals(currentBalanceFirstCard + amountTransfer, dashboardPage.getCardBalance(0));
    }

    @Test
    void shouldTransferMoneyWithAmountTransferOverLimit() {
        var dashboardPage = new DashboardPage();
        var cardTransferPage = dashboardPage.firstCardTransfer();
        cardTransferPage.moneyTransfer(amountTransferOverLimit, DataHelper.getSecondCardInfo());
        cardTransferPage.errorMessage();
    }
}
