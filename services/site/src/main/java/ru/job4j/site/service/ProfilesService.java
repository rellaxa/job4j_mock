package ru.job4j.site.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.ProfileDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CheckDev пробное собеседование
 * ProfileService класс обработки логики с моделью ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 23.09.2023T03:05
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProfilesService {
	private static final String URL_PROFILES = "/profiles/";
	private final WebClientAuthCall webClientAuthCall;
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Метод получает из сервиса Auth один профиль по ID
	 *
	 * @param id Int ID
	 * @return Optional<ProfileDTO>
	 */
	public Optional<ProfileDTO> getProfileById(int id) {
		var uri = URL_PROFILES + id;
		ResponseEntity<ProfileDTO> profile = webClientAuthCall
				.doGetReqParam(uri)
				.block();
		return Optional.ofNullable(profile.getBody());
	}

	/**
	 * Метод получает из сервиса Auth список всех профилей.
	 *
	 * @return List<ProfileDTO>
	 */
	public List<ProfileDTO> getAllProfile() {
		var responseEntity = webClientAuthCall
				.doGetReqParamAll(URL_PROFILES)
				.block();
		return responseEntity.getBody();
	}

	public Set<ProfileDTO> getByInterviews(List<InterviewDTO> interviews) {
		return interviews.stream()
				.map(interview -> getProfileById(interview.getSubmitterId()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
	}
}
