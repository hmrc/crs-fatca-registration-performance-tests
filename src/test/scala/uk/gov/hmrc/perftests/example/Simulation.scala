/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.example

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.example.Requests._

class Simulation extends PerformanceTestRunner {

  setup("AuthLogin", "logging in via auth").withRequests (
    getAuthLoginPage,
    postAuthLoginPage
  )

  setup("BusinessWithoutIdForOrganisation", "Business without Id Journey").withActions (
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypePage,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage("false"),
    getHaveUTRPage,
    postHaveUTRNoPage,
    getBusinessNamePage,
    postBusinessNamePage,
    getHaveTradeNamePage,
    postHaveTradeNamePage,
    getTradeNamePage,
    postTradeNamePage,
    getMainBusinessAddressPage,
    postMainBusinessAddressPage
  )

  setup("ContactDetails", "Contact Details Journey") withRequests (
    getYourContactDetailsPage,
    getContactNamePage,
    postContactNamePage,
    getEmailPage,
    postEmailPage,
    getHavePhonePage,
    postHavePhonePage,
    getPhonePage,
    postPhonePage,
    getSecondContactPage,
    postSecondContactPage,
    postSecondContactNamePage,
    getSecondContactEmailPage,
    postSecondContactEmailPage,
    getSecondContactHavePhonePage,
    postSecondContactHavePhonePage,
    getSecondContactPhonePage,
    postSecondContactPhonePage,
    getCheckYourAnswersPage,
    postCheckYourAnswersPage,
    getConfirmationRegistrationPage
  )

  setup("BusinessWithIdForOrganisation", "Business with Id Journey").withActions (
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypePage,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage("true"),
    getUTRPage,
    postUTRPage,
    getRegisteredBusinessNameWithIdPage,
    postRegisteredBusinessNameWithIdPage,
    getYourBusinessWithIdPage,
    postYourBusinessWithIdPage,
  )

  setup("SoletraderWithoutId", "Soletrader Without ID Journey").withActions(
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypeSole,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage("false"),
    getUTRPage,
    postHaveUTRNoSTPage,
    getNINOPage,
    postHaveNINONoSTPage,
    getWhatIsYourNamePage,
    postWhatIsYourNamePage,
    getWhatIsYourDateOfBirthPage,
    postWhatIsYourDateOfBirthPage,
    getWhereDoYouLivePage,
    postWhereDoYouLivePage,
    getUserAddressNonUKPage,
    postUserAddressNonUKPage
  )

  setup("IndividualContactDetails", "Individual Contact Details Journey") withRequests (
    getIndividualEmailAddressPage,
    postIndividualEmailAddressPage,
    getIndividualHavePhonePage,
    postIndividualHavePhonePage,
    getIndividualPhonePage,
    postIndividualPhonePage
  )

  setup("SoletraderWithId", "Sole trader With ID Journey"). withActions(
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypeSole,
    getRegisteredAddressInUkPage,
    postRegisteredAddressInUkPage("true"),
    getUTRPage,
    postIndividualUTRPage
  )

  setup("IndividualWithoutId", "Individual Without ID Journey").withActions(
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypeIndividual
  )

  setup("IndividualWithId", "Individual With ID Journey"). withActions(
    getBusinessRegistrationTypePage,
    postBusinessRegistrationTypeIndividual,
    getNINOPage,
    postNINOYesPage,
    getNINumberPage,
    postNINumberPage
  )


  runSimulation()
}
