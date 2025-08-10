package ru.checkdev.notification.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Petr Arsentev (parsentev@yandex.ru)
 * @version $Id$
 * @since 0.1
 */
@Setter
@Getter
@Entity(name = "setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.ORDINAL)
    private Key key;

    private String value;

    public Setting() {
    }

    public Setting(Key key, String value) {
        this.key = key;
        this.value = value;
    }

	public enum Key {
        HOST, PORT, AUTH, FROM, USERNAME, PASSWORD
    }
}
