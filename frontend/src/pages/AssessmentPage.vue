<template>
  <div>
    <h3>Assessment For {{ topicConstructor }} </h3>
    <h5>Passing criteria: {{ passCriteriaPercentage }}%</h5>
    <div>
      <div v-if="errors != ''">
        {{ errors }}
      </div>
      <QuizQuestionComp
        v-else-if="currentQuestion < quizQuestions.length"
        :quiz-question="quizQuestions[currentQuestion]!"
        @answered="questionAnswered"
      />
      <div v-else-if="assessmentResult">
        <p>Your score: {{ correctAnswers }} / {{ quizQuestions.length }}</p>
        <div class="alert alert-success" v-if="assessmentResult?.attempt?.isPass">
          You have passed the assessment.
        </div>
        <PopButton
          :disabled="!generatesCertificate"
          disabledTitle="This notebook does not award a certificate."
          btn-class="btn btn-light"
          title="View Certificate"
          v-if="assessmentResult?.attempt?.isPass"
        >
          <CertificatePopup :assessment-attempt="assessmentResult.attempt" :notebook-id="certificate?.notebook?.id"></CertificatePopup>
        </PopButton>
        <div class="alert alert-danger" v-else>
          You have not passed the assessment.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import useLoadingApi from "@/managedApi/useLoadingApi"
import {
  QuizQuestion,
  AnswerSubmission,
  AssessmentResult,
  Certificate,
} from "@/generated/backend"
import { useRouter } from "vue-router"
import QuizQuestionComp from "@/components/review/QuizQuestion.vue"

const { managedApi } = useLoadingApi()
const router = useRouter()
const props = defineProps({
  notebookId: { type: Number, required: true },
  approvalStatus: { type: String, required: true },
})

const topicConstructor = computed(() => {
  return router.currentRoute.value.query?.topic
})

const generatesCertificate = computed(() => {
  return props.approvalStatus === "APPROVED"
})

const quizQuestions = ref<QuizQuestion[]>([])
const currentQuestion = ref(0)
const errors = ref("")
const correctAnswers = ref(0)
const assessmentResult = ref<AssessmentResult | undefined>(undefined)
const questionsAnswerCollection = ref<AnswerSubmission[]>([])
const certificate = ref<Certificate>()

const passCriteriaPercentage = 80

const questionAnswered = async (answerResult) => {
  questionsAnswerCollection.value.push({
    questionId: quizQuestions.value[currentQuestion.value]!.id,
    answerId: answerResult.answerId,
    correctAnswers: answerResult.correct,
  })
  if (answerResult.correct) {
    correctAnswers.value += 1
  }
  currentQuestion.value += 1
  if (
    currentQuestion.value >= quizQuestions.value.length &&
    quizQuestions.value.length > 0
  ) {
    assessmentResult.value =
      await managedApi.restAssessmentController.submitAssessmentResult(
        props.notebookId,
        questionsAnswerCollection.value
      )
    certificate.value =
      await managedApi.restCertificateController.saveCertificate(
        props.notebookId
      )
  }
}

const generateAssessmentQuestions = async () => {
  quizQuestions.value =
    await managedApi.restAssessmentController.generateAssessmentQuestions(
      props.notebookId
    )
}

onMounted(() => {
  generateAssessmentQuestions()
})
</script>
