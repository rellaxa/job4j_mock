package ru.checkdev.notification.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author olegbelov
 * @since 20.12.2016
 */
@Setter
@Getter
@Entity(name = "template")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    private String subject;

    private String body;

    public Template() {
    }

    public Template(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        return id == template.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
