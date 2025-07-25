package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;

@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {

	private final TgAuthCallWebClient authCallWebClint;

	private static final String URL_AUTH_PROFILES = "/profiles/chatId/";

	@Override
	public BotApiMethod<Message> handle(Message message) {
		var chatId = message.getChatId().toString();
		var username = message.getFrom().getUserName();
		var text = "";
		var sl = System.lineSeparator();

		PersonDTO findPerson;
		try {
			findPerson = authCallWebClint.doGet(URL_AUTH_PROFILES + message.getChatId()).block();
		} catch (Exception e) {
			log.error("WebClient doGet error: {}", e.getMessage());
			text = "Сервис не доступен попробуйте позже" + sl
					+ "/start";
			return new SendMessage(chatId, text);
		}

		if (findPerson == null) {
			text = "Телеграм аккаунта с username: " + username + " нет в системе, " + sl
					+ "зарегистрируйтесь: /new " + sl
					+ "или свяжите этот аккаунт по email: /bind";
			return new SendMessage(chatId, text);
		}

		var fio = findPerson.getFio();
		var email = findPerson.getEmail();
		text = "ФИО: " + fio + sl
				+ "email: " + email;
		return new SendMessage(chatId, text);
	}

	@Override
	public BotApiMethod<Message> callback(Message message) {
		return handle(message);
	}
}
