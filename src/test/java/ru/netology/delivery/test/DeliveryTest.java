package ru.netology.delivery.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


class DeliveryTest {

    public String setDate(int plusDays) {
        return LocalDate.now().plusDays(plusDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @ParameterizedTest
    @CsvSource({
            "Майкоп", "Горно-Алтайск", "Уфа", "Улан-Удэ", "Махачкала", "Донецк", "Магас", "Нальчик", "Элиста", "Черкесск",
            "Петрозаводск", "Сыктывкар", "Симферополь", "Луганск", "Йошкар-Ола", "Саранск", "Якутск", "Владикавказ", "Казань", "Кызыл",
            "Ижевск", "Абакан", "Грозный", "Чебоксары", "Барнаул", "Чита", "Петропавловск-Камчатский", "Краснодар", "Красноярск", "Пермь",
            "Владивосток", "Ставрополь", "Хабаровск", "Благовещенск", "Архангельск", "Астрахань", "Белгород", "Брянск", "Владимир", "Волгоград",
            "Вологда", "Воронеж", "Мелитополь", "Иваново", "Иркутск", "Калининград", "Калуга", "Кемерово", "Киров", "Кострома",
            "Курган", "Курск", "Гатчина", "Санкт-Петербург", "Липецк", "Магадан", "Красногорск", "Мурманск", "Нижний Новгород", "Великий Новгород",
            "Новосибирск", "Омск", "Оренбург", "Орёл", "Пенза", "Псков", "Ростов-на-Дону", "Рязань", "Самара", "Саратов",
            "Южно-Сахалинск", "Екатеринбург", "Смоленск", "Тамбов", "Тверь", "Томск", "Тула", "Тюмень", "Ульяновск", "Херсон",
            "Челябинск", "Ярославль", "Москва", "Санкт-Петербург", "Севастополь", "Биробиджан", "Нарьян-Мар", "Ханты-Мансийск", "Анадырь", "Салехард"
    })
    void allTownTesting(String administrativeCenter) {

        $("[data-test-id='city'] .input__control").setValue(administrativeCenter);
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(setDate(3));
        $("[data-test-id='name'] .input__control").setValue("Имя");
        $("[data-test-id='phone'] .input__control").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $("button span.button__text").click();

        $("[data-test-id=city].input_invalid .input__sub").shouldNotBe(visible);
        $(".notification__content").shouldBe(visible, Duration.ofSeconds(2));
        $("[data-test-id='success-notification'] .notification__content").shouldHave(exactText("Встреча успешно запланирована на " + setDate(3)));

    }

    @ParameterizedTest
    @CsvSource({
        "Игорь-Геннадьевич", "Фёкла Васильевна"
    })
    void nameTesting(String name) {

        $("[data-test-id='city'] .input__control").setValue("Майкоп");
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(setDate(3));
        $("[data-test-id='name'] .input__control").setValue(name);
        $("[data-test-id='phone'] .input__control").setValue("+79999999999");
        $("[data-test-id='agreement']").click();
        $("button span.button__text").click();

        $("[data-test-id=city].input_invalid .input__sub").shouldNotBe(visible);
        $(".notification__content").shouldBe(visible, Duration.ofSeconds(2));
        $("[data-test-id='success-notification'] .notification__content").shouldHave(exactText("Встреча успешно запланирована на " + setDate(3)));

    }

    @ParameterizedTest
    @CsvSource({
            "Alex", // латиница
            "Турчинский~", // спецсимволы
            "Турчинский!", // спецсимволы
            "Турчинский№", // спецсимволы
            "Турчинский;", // спецсимволы
            "Турчинский%", // спецсимволы
            "Турчинский:", // спецсимволы
            "Турчинский?", // спецсимволы
            "Турчинский*", // спецсимволы
            "Турчинский+", // спецсимволы
            "Турчинский=", // спецсимволы
            "Турчинский/", // спецсимволы
            "Турчинский.", // спецсимволы
            "123," // цифры
    })
    void shouldNotTakeName(String name) {

        $("[data-test-id='city'] .input__control").setValue("Москва");
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(setDate(3));
        $("[data-test-id='name'] .input__control").setValue(name);
        $("[data-test-id='phone'] .input__control").setValue("+79996665544");
        $("[data-test-id='agreement']").click();
        $("button span.button__text").click();
        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));

    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id='city'] .input__control").setValue(validUser.getCity());
        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] .input__control").setValue(validUser.getName());
        $("[data-test-id='phone'] .input__control").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $x("//button//span[text()='Запланировать']").click();
        $("[data-test-id='success-notification'] .notification__content").
                shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));

        $("[data-test-id='date'] .input__control").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $x("//button//span[text()='Запланировать']").click();
        $("div[data-test-id='replan-notification'] .notification__content").shouldBe(visible);
        $x("//button//span[text()='Перепланировать']").click();
        $("[data-test-id='success-notification'] div.notification__content").
                shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));

    }

}
