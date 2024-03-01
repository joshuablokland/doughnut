import { flushPromises } from "@vue/test-utils";
import NoteContent from "@/components/notes/NoteContent.vue";
import ManagedApi from "@/managedApi/ManagedApi";
import createNoteStorage from "../../src/store/createNoteStorage";
import makeMe from "../fixtures/makeMe";
import helper from "../helpers";

describe("undo editing", () => {
  it("should call addEditingToUndoHistory on submitChange", async () => {
    const histories = createNoteStorage(
      new ManagedApi({ errors: [], states: [] }),
    );

    const noteRealm = makeMe.aNoteRealm
      .topicConstructor("Dummy Title")
      .please();
    histories.refreshNoteRealm(noteRealm);

    const updatedTitle = "updated";
    const wrapper = helper
      .component(NoteContent)
      .withProps({
        note: noteRealm.note,
        storageAccessor: histories,
      })
      .mount();

    await wrapper.find('[role="topic"]').trigger("click");
    await wrapper.find('[role="topic"] input').setValue(updatedTitle);
    await wrapper.find('[role="topic"] input').trigger("blur");
    await flushPromises();

    expect(histories.peekUndo()).toMatchObject({ type: "edit topic" });
  });
});
