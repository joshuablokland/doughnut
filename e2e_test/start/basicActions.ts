import { assumeAnsweredQuestionPage } from './pageObjects/AnsweredQuestionPage'
import { assumeAssessmentPage } from './pageObjects/AssessmentPage'
import { assumeQuestionPage } from './pageObjects/QuizQuestionPage'
import { assumeAdminDashboardPage } from './pageObjects/adminPages/adminDashboardPage'
import { navigateToBazaar } from './pageObjects/bazaarPage'
import { assumeChatAboutNotePage } from './pageObjects/chatAboutNotePage'
import { navigateToCircle } from './pageObjects/circlePage'
import { assumeClarifyingQuestionDialog } from './pageObjects/clarifyingQuestionDialog'
import { assumeNotePage } from './pageObjects/notePage'
import { routerToNotebooksPage } from './pageObjects/notebooksPage'
import { noteSidebar } from './pageObjects/noteSidebar'
import { systemSidebar } from './pageObjects/systemSidebar'
import testability from './testability'

export default {
  navigateToBazaar,
  noteSidebar,
  systemSidebar,
  assumeNotePage,
  assumeAssessmentPage,
  assumeAnsweredQuestionPage,
  assumeChatAboutNotePage,
  assumeQuestionPage,
  assumeAdminDashboardPage,
  assumeClarifyingQuestionDialog,
  routerToNotebooksPage,
  navigateToCircle,

  // jumptoNotePage is faster than navigateToPage
  //    it uses the note id memorized when creating them with testability api
  jumpToNotePage: (noteTopic: string, forceLoadPage = false) => {
    testability()
      .getInjectedNoteIdByTitle(noteTopic)
      .then((noteId) => {
        const url = `/n${noteId}`
        if (forceLoadPage) cy.visit(url)
        else cy.routerPush(url, 'noteShow', { noteId: noteId })
      })

    return assumeNotePage(noteTopic)
  },

  loginAsAdmin: () => {
    cy.logout()
    cy.loginAs('admin')
  },

  goToAdminDashboard: () => {
    cy.reload()
    return systemSidebar().adminDashboard()
  },

  loginAsAdminAndGoToAdminDashboard() {
    this.loginAsAdmin()
    return this.goToAdminDashboard()
  },
  navigateToAssessmentAndCertificatePage() {
    return systemSidebar().userOptions().myAssessmentAndCertificateHistory()
  },
}
