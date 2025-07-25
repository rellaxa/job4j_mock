package ru.checkdev.notification.telegram.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс дополнительных функций телеграм бота, проверка почты, генерация пароля.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
public class TgConfig {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
	private final String prefix;
	private final int passSize;

	public TgConfig(String prefix, int passSize) {
		this.prefix = prefix;
		this.passSize = passSize;
	}

	/**
	 * Метод проверяет входящую строку на соответствие формату email
	 *
	 * @param email String
	 * @return boolean
	 */
	public boolean isEmail(String email) {
		Matcher matcher = EMAIL_PATTERN.matcher(email);
		return matcher.matches();
	}

	/**
	 * метод генерирует пароль для пользователя
	 *
	 * @return String
	 */
	public String getPassword() {
		String password = prefix + UUID.randomUUID();
		return password.substring(0, passSize);
	}

	/**
	 * Метод преобразовывает Object в карту Map<String,String>
	 *
	 * @param object Object or Person(Auth)
	 * @return Map
	 */
	public Map<String, String> getMapFromObject(Object object) {
		return MAPPER.convertValue(object, Map.class);
	}

	/**
	 * Метод преобразовывает Map<String,String> в Object
	 *
	 * @param object Map<String, String>
	 * @return Object
	 */
	public Object getObjectFromMap(Object object, Class<?> clazz) {
		return MAPPER.convertValue(object, clazz);
	}

	/**
	 * Метод парсит String message
	 * и возвращает map с fio и email
	 * @param message String
	 * @return Map
	 */
	public Optional<Map<String, String>> getFioAndEmail(String message) {
		var strings = message.trim().split("\\s+");
		if (strings.length != 4) {
			return Optional.empty();
		}
		return Optional.of(
				Map.of(
						"fio", String.format("%s %s %s", strings[0], strings[1], strings[2]),
						"email", strings[3]
				)
		);
	}

	/**
	 * Метод парсит String message
	 * и возвращает map с email и password
	 * @param message String
	 * @return Map
	 */
	public Optional<Map<String, String>> getEmailAndPassword(String message) {
		var strings = message.trim().split("\\s+");
		if (strings.length != 2) {
			return Optional.empty();
		}
		return Optional.of(
				Map.of(
						"email", strings[0],
						"password", strings[1]
				)
		);
	}
}
