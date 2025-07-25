package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;

/**
 * DTO модель класса Person сервиса Auth.
 *
 * @author parsentev
 * @since 25.09.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDTO {

    private Long chatId;

    private String username;

    private String fio;

    private String email;

    private String password;

    private boolean privacy;

    private List<RoleDTO> roles;

    private Calendar created;

}
