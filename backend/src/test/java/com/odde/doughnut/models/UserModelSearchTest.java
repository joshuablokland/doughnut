package com.odde.doughnut.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.odde.doughnut.controllers.json.SearchTerm;
import com.odde.doughnut.entities.Circle;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.testability.MakeMe;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserModelSearchTest {
  @Autowired MakeMe makeMe;
  SearchTermModel searchTermModel;
  User user;
  UserModel anotherUser;
  Note note;
  final SearchTerm searchTerm = new SearchTerm();

  @BeforeEach
  void setup() {
    user = makeMe.aUser().please();
    note = makeMe.aNote().creatorAndOwner(user).please();
    searchTerm.note = note;
    searchTermModel =
        new SearchTermModel(user, makeMe.modelFactoryService.noteRepository, searchTerm);
    anotherUser = makeMe.aUser().toModelPlease();
  }

  private List<Note> search() {
    return searchTermModel.searchForNotes();
  }

  @Test
  void returnNullWhenNoteKeyIsGiven() {
    assertTrue(search().isEmpty());
  }

  @Test
  void theNoteItselfIsNotIncludedInTheResult() {
    searchTerm.setSearchKey(note.getTopicConstructor());
    assertTrue(search().isEmpty());
  }

  @Test
  void theSearchIsCaseInsensitive() {
    Note anotherNote = makeMe.aNote("Some Note").under(note).please();
    searchTerm.setSearchKey("not");
    assertThat(search(), contains(anotherNote));
  }

  @Test
  void theSearchResultShouldNotIncludeSoftDeletedNote() {
    makeMe.aNote("Some Note").under(note).softDeleted().please();
    searchTerm.setSearchKey("not");
    assertTrue(search().isEmpty());
  }

  @Nested
  class ThereIsANoteBookInMyCircle {
    Note noteInTheSameNotebook;
    Note noteFromMyOtherNotebook;
    Note circleNote;
    Note bazaarNote;
    Note subscribedBazaarNote;
    final String commonPhrase = "viva la";

    @BeforeEach
    void setupBazaarNotes() {
      bazaarNote = makeMe.aNote(commonPhrase + " bazaar").creatorAndOwner(anotherUser).please();
      makeMe.aBazaarNodebook(bazaarNote.getNotebook()).please();
      subscribedBazaarNote =
          makeMe.aNote(commonPhrase + " subscription").creatorAndOwner(anotherUser).please();
      makeMe.aBazaarNodebook(subscribedBazaarNote.getNotebook()).please();
      makeMe.aSubscription().forNotebook(subscribedBazaarNote.getNotebook()).forUser(user).please();
    }

    @BeforeEach
    void setup() {
      noteInTheSameNotebook = makeMe.aNote(commonPhrase + " same notebook").under(note).please();
      noteFromMyOtherNotebook =
          makeMe.aNote(commonPhrase + " other notebook").creatorAndOwner(user).please();
      Circle circle = makeMe.aCircle().hasMember(user).hasMember(anotherUser).please();
      circleNote = makeMe.aNote(commonPhrase + " circle").inCircle(circle).please();
    }

    @ParameterizedTest
    @CsvSource({
      "false, false, false, false, false, 1",
      "true,  false, true,  true,  false, 3",
      "true,  true,  true,  true,  true, 4",
    })
    void testSearch(
        boolean allMyNotebooksAndSubscriptions,
        boolean allMyCircle,
        boolean expectOtherNotebooks,
        boolean expectSubscription,
        boolean expectCircleNote,
        int expectedCount) {
      searchTerm.setSearchKey(commonPhrase);
      searchTerm.setAllMyNotebooksAndSubscriptions(allMyNotebooksAndSubscriptions);
      searchTerm.setAllMyCircles(allMyCircle);
      List<Note> actual = search();
      assertThat(actual, hasSize(expectedCount));
      assertThat(
          actual,
          containsInAnyOrder(
              expectedNotes(expectOtherNotebooks, expectSubscription, expectCircleNote)));
    }

    Object[] expectedNotes(
        boolean expectOtherNotebooks, boolean expectSubscription, boolean expectCircleNote) {
      List<Note> result = new ArrayList<>();
      result.add(noteInTheSameNotebook);
      if (expectOtherNotebooks) result.add(noteFromMyOtherNotebook);
      if (expectSubscription) result.add(subscribedBazaarNote);
      if (expectCircleNote) result.add(circleNote);
      return result.toArray();
    }
  }
}
