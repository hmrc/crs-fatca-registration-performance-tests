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

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("crs-fatca-registration-frontend")
  val baseUrlAuth: String = baseUrlFor("auth-frontend")
  val route : String = "/register-for-crs-and-fatca"
//  val crsFatcaRegistrationRoute: String   = s"$baseUrl/register-for-crs-and-fatca"

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .check(status.is(200))

  val postAuthLoginPage: HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(baseUrlAuth + "/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("redirectionUrl", baseUrl + route)
      .check(status.is(303))
      .check(header("Location")
          .is(baseUrl + route)
          .saveAs("AuthLoginForCRS")
      )

  val getBusinessRegistrationTypePage: HttpRequestBuilder =
    http("Get Business Registration Type")
      .get(baseUrl + route + "/register/registration-type")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postBusinessRegistrationTypePage: HttpRequestBuilder =
    http("Post Business Registration Type")
      .post(baseUrl + route + "/register/registration-type")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "limited")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/registered-address-in-uk").saveAs("RegisteredAddressInUk"))

  val getRegisteredAddressInUkPage: HttpRequestBuilder =
    http("Get Registered Address In UK")
      .get(baseUrl + "${RegisteredAddressInUk}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  def postRegisteredAddressInUkPage(answer: String): HttpRequestBuilder = {
    val expectedRedirect = if (answer == "true") route + "/register/utr" else route + "/register/have-utr"
    http("Post Registered Address In UK")
      .post(baseUrl + "${RegisteredAddressInUk}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", answer)
      .check(status.is(303))
      .check(header("Location").is(expectedRedirect).saveAs("HaveUTR"))
  }

  val getHaveUTRPage: HttpRequestBuilder =
    http("Get Have UTR Page")
      .get(baseUrl + "${HaveUTR}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveUTRNoPage: HttpRequestBuilder =
    http("post Have UTR Page-No")
      .post(baseUrl + "${HaveUTR}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "false")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/without-id/business-name").saveAs("BusinessWithoutId"))

//  val getUTRPage: HttpRequestBuilder =
//    http("Get Have UTR Page")
//      .get(baseUrl + "${HaveUTR}")
//      .check(status.is(200))
//      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
//
//  val postUTRPage: HttpRequestBuilder =
//    http("post Have UTR Page-No")
//      .post(baseUrl + "${HaveUTR}")
//      .formParam("csrfToken", "${csrfToken}")
//      .formParam("value", "1234567890")
//      .check(status.is(303))
//      .check(header("Location").is(route + "/register/business-name").saveAs("BusinessWithID"))

  val getBusinessNamePage: HttpRequestBuilder =
    http("Get Business Name Page without Id")
      .get(baseUrl + "${BusinessWithoutId}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postBusinessNamePage: HttpRequestBuilder =
    http("post Business name without id")
      .post(baseUrl + "${BusinessWithoutId}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "CRSFATCA company")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/without-id/have-trading-name").saveAs("HaveTradeName"))

  val getHaveTradeNamePage: HttpRequestBuilder =
    http("Get Have Trade name page")
      .get(baseUrl + "${HaveTradeName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveTradeNamePage: HttpRequestBuilder =
    http("post Trade name")
      .post(baseUrl + "${HaveTradeName}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/without-id/trading-name").saveAs("TradingName"))

  val getTradeNamePage: HttpRequestBuilder =
    http("Get Trade name page")
      .get(baseUrl + "${TradingName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postTradeNamePage: HttpRequestBuilder =
    http("post Trading name")
      .post(baseUrl + "${TradingName}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "ABC company")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/without-id/business-address").saveAs("Address"))

  val getMainBusinessAddressPage: HttpRequestBuilder =
    http("Get Business Address page")
      .get(baseUrl + "${Address}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postMainBusinessAddressPage: HttpRequestBuilder =
    http("post Business Address")
      .post(baseUrl + "${Address}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("addressLine1", "1 Test Street")
      .formParam("addressLine2", "Test Area")
      .formParam("addressLine3", "TestCity")
      .formParam("addressLine4", "TestRegion")
      .formParam("postCode", "AB12 6XX")
      .formParam("country", "PL")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/your-contact-details").saveAs("YourContactDetails"))

  val getYourContactDetailsPage: HttpRequestBuilder =
    http("Get your contact details page")
      .get(baseUrl + "${YourContactDetails}")
      .check(status.is(200))

  val getContactNamePage: HttpRequestBuilder =
    http("Get your contact name page")
      .get(baseUrl + route + "/register/contact-name")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postContactNamePage: HttpRequestBuilder =
    http("post Contact name")
      .post(baseUrl + route + "/register/contact-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "Tester Name")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/email").saveAs("Email"))

  val getEmailPage: HttpRequestBuilder =
    http("Get email")
      .get(baseUrl + "${Email}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postEmailPage: HttpRequestBuilder =
    http("post email")
      .post(baseUrl + "${Email}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "test@verify.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-phone").saveAs("HavePhone"))

  val getHavePhonePage: HttpRequestBuilder =
    http("Get have-phone")
      .get(baseUrl + "${HavePhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHavePhonePage: HttpRequestBuilder =
    http("post have-phone")
      .post(baseUrl + "${HavePhone}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/phone").saveAs("Phone"))

  val getPhonePage: HttpRequestBuilder =
    http("Get phone")
      .get(baseUrl + "${Phone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postPhonePage: HttpRequestBuilder =
    http("post phone")
      .post(baseUrl + "${Phone}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/have-second-contact").saveAs("SecondContact"))

  val getSecondContactPage: HttpRequestBuilder =
    http("Get Second Contact")
      .get(baseUrl + "${SecondContact}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactPage: HttpRequestBuilder =
    http("post Second Contact")
      .post(baseUrl + "${SecondContact}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-name").saveAs("SecondContactName"))


  val getSecondContactNamePage: HttpRequestBuilder =
    http("Get Second Contact Name")
      .get(baseUrl + "${SecondContactName}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactNamePage: HttpRequestBuilder =
    http("post Second Contact Name")
      .post(baseUrl + "${SecondContactName}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "Test Second Contact")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-email").saveAs("SecondContactEmail"))

  val getSecondContactEmailPage: HttpRequestBuilder =
    http("Get Second Contact Email")
      .get(baseUrl + "${SecondContactEmail}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactEmailPage: HttpRequestBuilder =
    http("post Second Contact Email")
      .post(baseUrl + "${SecondContactEmail}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "test2@test.com")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-have-phone").saveAs("SecondContactHavePhone"))

  val getSecondContactHavePhonePage: HttpRequestBuilder =
    http("Get Second Contact Have Phone")
      .get(baseUrl + "${SecondContactHavePhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactHavePhonePage: HttpRequestBuilder =
    http("post Second Contact Have Phone")
      .post(baseUrl + "${SecondContactHavePhone}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/second-contact-phone").saveAs("SecondContactPhone"))

  val getSecondContactPhonePage: HttpRequestBuilder =
    http("Get Second Contact Phone")
      .get(baseUrl + "${SecondContactPhone}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactPhonePage: HttpRequestBuilder =
    http("post Second Contact Phone")
      .post(baseUrl + "${SecondContactPhone}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "1234567999")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/check-your-answers").saveAs("CheckYourAnswers"))

  val getCheckYourAnswersPage: HttpRequestBuilder =
    http("Get Check Your Answers")
      .get(baseUrl + "${CheckYourAnswers}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckYourAnswersPage: HttpRequestBuilder =
    http("post Check Your Answers")
      .post(baseUrl + "${CheckYourAnswers}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/confirm-registration").saveAs("ConfirmRegistrationPage"))

  val getConfirmationRegistrationPage: HttpRequestBuilder =
    http("Get Confirmation Registration Page")
      .get(baseUrl + "${ConfirmRegistrationPage}")
      .check(status.is(200))


  val getUTRPage: HttpRequestBuilder =
      http("Get Have UTR Page")
        .get(baseUrl + "${HaveUTR}")
        .check(status.is(200))
        .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

    val postUTRPage: HttpRequestBuilder =
      http("post Have UTR Page-No")
        .post(baseUrl + "${HaveUTR}")
        .formParam("csrfToken", "${csrfToken}")
        .formParam("value", "1234567890")
        .check(status.is(303))
        .check(header("Location").is(route + "/register/business-name").saveAs("BusinessWithID"))

  val getRegisteredBusinessNameWithIdPage: HttpRequestBuilder =
    http("Get Business Name Page with Id")
      .get(baseUrl + "${BusinessWithID}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postRegisteredBusinessNameWithIdPage: HttpRequestBuilder =
    http("post Business name with id")
      .post(baseUrl + "${BusinessWithID}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "CRS FATCA company")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/is-this-your-business").saveAs("YourBusinessPage"))

  val getYourBusinessWithIdPage: HttpRequestBuilder =
    http("Get Your Business Page")
      .get(baseUrl + "${YourBusinessPage}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postYourBusinessWithIdPage: HttpRequestBuilder =
    http("post Your Business Page")
      .post(baseUrl + "${YourBusinessPage}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").is(route + "/register/your-contact-details").saveAs("YourContactDetails"))








}
