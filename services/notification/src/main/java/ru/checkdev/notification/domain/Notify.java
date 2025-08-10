package ru.checkdev.notification.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * @author Petr Arsentev (parsentev@yandex.ru)
 * @version $Id$
 * @since 0.1
 */
@Setter
@Getter
public class Notify {

    private String template;

    private String email;

    private Map<String, ?> keys;

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Notify notify = (Notify) o;

        return Objects.equals(template, notify.template);
    }

    @Override
    public int hashCode() {
        return template != null ? template.hashCode() : 0;
    }
}
