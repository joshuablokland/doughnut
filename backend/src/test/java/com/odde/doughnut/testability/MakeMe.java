package com.odde.doughnut.testability;

import com.odde.doughnut.models.Note;
import com.odde.doughnut.models.User;
import com.odde.doughnut.testability.builders.BazaarNoteBuilder;
import com.odde.doughnut.testability.builders.NoteBuilder;
import com.odde.doughnut.testability.builders.UserBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.nio.CharBuffer;

@Component
public class MakeMe {
    public UserBuilder aUser() {
        return new UserBuilder(this);
    }

    public NoteBuilder aNote() {
        return new NoteBuilder(this);
    }

    public BazaarNoteBuilder aBazaarNode(Note note) {
        return new BazaarNoteBuilder(this, note);
    }

    public <T> T refresh(EntityManager entityManager, T object) {
        entityManager.refresh(object);
        return object;
    }

    public String aStringOfLength(int length, char withChar) {
        return CharBuffer.allocate(length).toString().replace('\0', withChar);
    }

    public String aStringOfLength(int length) {
        return aStringOfLength(length, 'a');
    }
}
