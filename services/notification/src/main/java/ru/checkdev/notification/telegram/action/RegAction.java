package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;

import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
	private static final String ERROR_OBJECT = "error";
	private static final String URL_AUTH_REGISTRATION = "/registration";
	private static final String URL_AUTH_PROFILES = "/profiles/chatId/";
	private final TgConfig tgConfig = new TgConfig("tg/", 8);
	private final TgAuthCallWebClient authCallWebClint;
	private final String urlSiteAuth;

	@Override
	public BotApiMethod<Message> handle(Message message) {
		var chatId = message.getChatId().toString();
		var text = "Введите полное ФИО и email(все через пробел) для регистрации:";
		return new SendMessage(chatId, text);
	}

	/**
	 * Метод формирует ответ пользователю.
	 * Весь метод разбит на 4 этапа проверки.
	 * 1. Проверка на соответствие формату Email введенного текста.
	 * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
	 * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
	 * 3.1 ответ при ошибке регистрации
	 * 3.2 ответ при успешной регистрации.
	 *
	 * @param message Message
	 * @return BotApiMethod<Message>
	 */
	@Override
	public BotApiMethod<Message> callback(Message message) {
		var chatId = message.getChatId().toString();
		var text = "";
		var sl = System.lineSeparator();

		var fioAndEmail = tgConfig.getFioAndEmail(message.getText());
		if (fioAndEmail.isEmpty()) {
			text = "Вы ввели некорректные данные, должно быть 4 отдельных слова в формате: <Фамилия> <Имя> <Отчество> <email>" + sl
					+ "попробуйте снова." + sl
					+ "/new";
			return new SendMessage(chatId, text);
		}
		var fio = fioAndEmail.get().get("fio");
		var email = fioAndEmail.get().get("email");
		var username = message.getFrom().getUserName();

		if (!tgConfig.isEmail(email)) {
			text = "Email: " + email + " не корректный." + sl
					+ "попробуйте снова." + sl
					+ "/new";
			return new SendMessage(chatId, text);
		}

		PersonDTO findPerson;
		try {
			findPerson = authCallWebClint.doGet(URL_AUTH_PROFILES + chatId).block();
		} catch (Exception e) {
			log.error("WebClient doGet error: {}", e.getMessage());
			text = "Сервис не доступен попробуйте позже" + sl
					+ "/start";
			return new SendMessage(chatId, text);
		}

		if (findPerson != null) {
			text = "Ошибка регистрации: аккаунт c username: %s уже зарегистрирован %s /start"
					.formatted(findPerson.getUsername(), sl);
			return new SendMessage(chatId, text);
		}

		var password = tgConfig.getPassword();
		var person = new PersonDTO(message.getChatId(), username, fio, email, password, true, null,
				Calendar.getInstance());
		Object result;
		try {
			result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
		} catch (Exception e) {
			log.error("WebClient doPost error: {}", e.getMessage());
			text = "Сервис не доступен попробуйте позже" + sl
					+ "/start";
			return new SendMessage(chatId, text);
		}

		var mapObject = tgConfig.getMapFromObject(result);

		if (mapObject.containsKey(ERROR_OBJECT)) {
			text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
			return new SendMessage(chatId, text);
		}

		text = "Вы зарегистрированы: " + sl
				+ "Логин: " + email + sl
				+ "Пароль: " + password + sl
				+ urlSiteAuth;
		return new SendMessage(chatId, text);
	}
}
