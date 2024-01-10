package com.bobocode.persistence;

import com.bobocode.persistence.annotation.Column;
import com.bobocode.persistence.annotation.Entity;
import com.bobocode.persistence.annotation.Id;
import com.bobocode.persistence.annotation.Table;

import java.util.Objects;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id && Objects.equals(title, note.title) && Objects.equals(description, note.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
