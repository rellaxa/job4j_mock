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

@AllArgsConstructor
@Slf4j
public class BindAction implements Action {

	private static final String URL_AUTH_BIND = "/bind";
	private static final String ERROR_OBJECT = "error";

	private final TgConfig tgConfig = new TgConfig("tg/", 8);;

	private TgAuthCallWebClient authCallWebClint;

	@Override
	public BotApiMethod<Message> handle(Message message) {
		var chatId = message.getChatId().toString();
		var text = "Введите логин и пароль через запятую:";
		return new SendMessage(chatId, text);
	}

	@Override
	public BotApiMethod<Message> callback(Message message) {
		var chatId = message.getChatId().toString();
		var username = message.getFrom().getUserName();
		var sl = System.lineSeparator();
		var text = "";

		var emailAndPassword = tgConfig.getEmailAndPassword(message.getText());
		if (emailAndPassword.isEmpty()) {
			text = "Вы ввели некорректные данные, должно быть 2 отдельных слова в формате: <email> <password>" + sl
					+ "попробуйте снова:" + sl
					+ "/bind";
			return new SendMessage(chatId, text);
		}
		var email = emailAndPassword.get().get("email");
		var password = emailAndPassword.get().get("password");

		if (!tgConfig.isEmail(email)) {
			text = "Email: " + email + " не корректный." + sl
					+ "попробуйте снова." + sl
					+ "/bind";
			return new SendMessage(chatId, text);
		}

		var person = PersonDTO.builder()
				.chatId(message.getChatId())
				.email(email)
				.password(password)
				.privacy(true)
				.roles(null)
				.created(Calendar.getInstance())
				.build();

		Object result;
		try {
			result = authCallWebClint.doPost(URL_AUTH_BIND, person).block();
		} catch (Exception e) {
			log.error("WebClient doPost error: {}", e.getMessage());
			text = "Сервис не доступен попробуйте позже" + sl
					+ "/start";
			return new SendMessage(chatId, text);
		}

		var mapObject = tgConfig.getMapFromObject(result);

		if (mapObject.containsKey(ERROR_OBJECT)) {
			text = "Не удалось связать аккаунт: " + mapObject.get(ERROR_OBJECT);
			return new SendMessage(chatId, text);
		}

		text = "Вы успешно привязали аккаунт telegram к платформе CheckDev";
		return new SendMessage(chatId, text);
	}
}
