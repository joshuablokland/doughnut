package com.odde.doughnut.models;

import com.odde.doughnut.entities.Answer;
import com.odde.doughnut.entities.AnswerResult;
import com.odde.doughnut.entities.AnswerViewedByUser;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestion;
import com.odde.doughnut.entities.SelfEvaluate;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import java.sql.Timestamp;
import java.util.List;

public class AnswerModel {
  private final Answer answer;
  private final ModelFactoryService modelFactoryService;
  private final QuizQuestion.QuestionType questionType;

  private Boolean cachedResult;

  public AnswerModel(Answer answer, ModelFactoryService modelFactoryService) {
    this.answer = answer;
    this.modelFactoryService = modelFactoryService;
    this.questionType = answer.getQuestion().getQuestionType();
  }

  public void updateReviewPoints(Timestamp currentUTCTimestamp) {
    SelfEvaluate selfEvaluate = getSelfEvaluate();
    answer
        .getQuestion()
        .getViceReviewPointIdList()
        .forEach(
            rPid ->
                this.modelFactoryService
                    .reviewPointRepository
                    .findById(rPid)
                    .ifPresent(
                        vice ->
                            this.modelFactoryService
                                .toReviewPointModel(vice)
                                .updateReviewPoint(currentUTCTimestamp, selfEvaluate)));
    ReviewPointModel reviewPointModel =
        this.modelFactoryService.toReviewPointModel(answer.getQuestion().getReviewPoint());
    reviewPointModel.updateReviewPoint(currentUTCTimestamp, selfEvaluate);
  }

  private SelfEvaluate getSelfEvaluate() {
    if (answer.getQuestion().getQuestionType() == QuizQuestion.QuestionType.JUST_REVIEW) {
      return SelfEvaluate.valueOf(answer.getSpellingAnswer());
    }
    return isCorrect() ? SelfEvaluate.satisfying : SelfEvaluate.sad;
  }

  public AnswerViewedByUser getAnswerViewedByUser() {
    AnswerViewedByUser answerResult = new AnswerViewedByUser();
    answerResult.answerId = answer.getId();
    answerResult.correct = isCorrect();
    answerResult.answerDisplay = getAnswerDisplay();
    return answerResult;
  }

  public AnswerResult getAnswerResult() {
    AnswerResult answerResult = new AnswerResult();
    answerResult.answerId = answer.getId();
    answerResult.correct = isCorrect();
    return answerResult;
  }

  public void save() {
    modelFactoryService.answerRepository.save(answer);
  }

  private String getAnswerDisplay() {
    if (getAnswerNote() != null) {
      return getAnswerNote().getTitle();
    }
    return answer.getSpellingAnswer();
  }

  private boolean isCorrect() {
    if (cachedResult != null) return cachedResult;
    List<Note> rightAnswers =
        questionType.presenter.apply(answer.getQuestion()).knownRightAnswers();
    cachedResult = rightAnswers.isEmpty() || rightAnswers.stream().anyMatch(this::matchAnswer);
    return cachedResult;
  }

  private Note getAnswerNote() {
    if (answer.getAnswerNoteId() == null) return null;
    return this.modelFactoryService.noteRepository.findById(answer.getAnswerNoteId()).orElse(null);
  }

  private boolean matchAnswer(Note correctAnswerNote) {
    if (getAnswerNote() != null) {
      return correctAnswerNote.equals(getAnswerNote());
    }

    return correctAnswerNote.getNoteTitle().matches(answer.getSpellingAnswer());
  }
}
