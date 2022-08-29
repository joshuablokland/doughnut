/**
 * @jest-environment jsdom
 */
import LinkNoteFinalize from "@/components/links/LinkNoteFinalize.vue";
import createHistory from "../../src/store/history";
import makeMe from "../fixtures/makeMe";
import helper from "../helpers";

describe("LinkNoteFinalize", () => {
  beforeEach(() => {
    helper.reset();
  });

  it("going back", async () => {
    const note = makeMe.aNoteRealm.please();
    const wrapper = helper
      .component(LinkNoteFinalize)
      .withProps({
        note,
        targetNote: note,
        historyWriter: createHistory(),
      })
      .mount();
    await wrapper.find(".go-back-button").trigger("click");
    expect(wrapper.emitted().goBack).toHaveLength(1);
  });
});
