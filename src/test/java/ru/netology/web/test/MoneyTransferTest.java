package ru.netology.web.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {

    String amountTransfer = String.valueOf(4350);
    String amountTransferWithFractionalRemainder = String.valueOf(678.87f);
    String amountTransferOverLimit = String.valueOf(15000);
    String amountTransferZero = String.valueOf(0);

    @BeforeEach
    public void setUpTest() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        Configuration.holdBrowserOpen = true;
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verifyInfo = DataHelper.getVerificationCode(authInfo);
        var dashboardPage = verificationPage.validVerify(verifyInfo);
        equalizingBalance();
    }

    public void equalizingBalance() {
        var dashboardPage = new DashboardPage();
        int currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        int currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        int amountTransfer;
        if (currentBalanceFirstCard > currentBalanceSecondCard) {
            amountTransfer = (currentBalanceFirstCard - currentBalanceSecondCard) / 2;
            var cardTransferPage = dashboardPage.secondCardTransfer();
            cardTransferPage.validMoneyTransfer(String.valueOf(amountTransfer), DataHelper.getFirstCardInfo());
        }
        if (currentBalanceSecondCard > currentBalanceFirstCard) {
            amountTransfer = (currentBalanceSecondCard - currentBalanceFirstCard) / 2;
            var cardTransferPage = dashboardPage.firstCardTransfer();
            cardTransferPage.validMoneyTransfer(String.valueOf(amountTransfer), DataHelper.getSecondCardInfo());
        }
    }

    @Test
    void shouldTransferMoneyFromFirstToSecond() {
        var dashboardPage = new DashboardPage();
        var currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        var currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        var cardTransferPage = dashboardPage.secondCardTransfer();
        cardTransferPage.validMoneyTransfer(amountTransfer, DataHelper.getFirstCardInfo());
        assertEquals(currentBalanceFirstCard - Integer.parseInt(amountTransfer), dashboardPage.getCardBalance(0));
        assertEquals(currentBalanceSecondCard + Integer.parseInt(amountTransfer), dashboardPage.getCardBalance(1));
    }

    @Test
    void shouldTransferMoneyFromSecondToFirst() {
        var dashboardPage = new DashboardPage();
        var currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        var currentBalanceSecondCard = dashboardPage.getCardBalance(1);
        var cardTransferPage = dashboardPage.firstCardTransfer();
        cardTransferPage.validMoneyTransfer(amountTransfer, DataHelper.getSecondCardInfo());
        assertEquals(currentBalanceSecondCard - Integer.parseInt(amountTransfer), dashboardPage.getCardBalance(1));
        assertEquals(currentBalanceFirstCard + Integer.parseInt(amountTransfer), dashboardPage.getCardBalance(0));
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondCardWithFractionalRemainder() {
        var dashboardPage = new DashboardPage();
        var currentBalanceFirstCard = dashboardPage.getCardBalance(0);
        var cardTransferPage = dashboardPage.secondCardTransfer();
        cardTransferPage.validMoneyTransfer(amountTransferWithFractionalRemainder, DataHelper.getFirstCardInfo());
        assertEquals(currentBalanceFirstCard - Float.parseFloat(amountTransferWithFractionalRemainder), dashboardPage.getCardBalance(0));
    }

    @Test
    void shouldTransferMoneyWithAmountTransferOverLimit() {
        var dashboardPage = new DashboardPage();
        var cardTransferPage = dashboardPage.firstCardTransfer();
        cardTransferPage.validMoneyTransfer(amountTransferOverLimit, DataHelper.getSecondCardInfo());
        cardTransferPage.errorMessage();
    }

    @Test
    void shouldTransferMoneyWithEmptyFromField() {
        var dashboardPage = new DashboardPage();
        var cardTransferPage = dashboardPage.firstCardTransfer();
        cardTransferPage.invalidMoneyTransfer(amountTransfer);
        cardTransferPage.errorMessage();
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondWithAmountTransferZero() {
        var dashboardPage = new DashboardPage();
        var cardTransferPage = dashboardPage.secondCardTransfer();
        cardTransferPage.validMoneyTransfer(amountTransferZero, DataHelper.getFirstCardInfo());
        cardTransferPage.errorMessage();
    }
}
