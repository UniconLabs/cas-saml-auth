package org.jasig.cas.web.flow

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * Plain Spring MVC controller running outside of the CAS' login web flow and acting as a Spring Security SAML callback handler endpoints
 * for both successful and failed authentication attempts with external IDPs.
 * <p/>
 * This implementation retrieves previously saved in HttpSession SWF execution URL for the currently executing login flow which was interrupted and paused
 * for external IDP authentication exchange. Once the SWF execution URL is retrieved, it appends <b>idpAuthnFinished</b> <i>_eventId</i> URL parameter
 * to signal a corresponding SWF event and finally forwards to that URL (running within the same Servlet context),
 * thus enabling SWF machinery to properly resume login flow execution at the point where it was interrupted by the external redirect to IDP endpoint.
 * <p/>
 * This controller is meant to be invoked by the Spring Security SAML framework with an HttpSession already established by CAS' login flow.
 * Do not attempt to manually invoke the mapped endpoint of this controller as it will explode in your face with a Runtime exception anyway :-)
 *
 * @author Dmitriy Kopylenko
 * @author JJ
 * @author Unicon, inc.
 */
@Controller
@RequestMapping("/idpAuthnFinishedCallback")
class LoginFlowResumingController {
    private static final String IDP_AUTHN_FINISHED_EVENT_ID_URL_PARAM = "&_eventId=idpAuthnFinished";

    @RequestMapping(method = RequestMethod.GET)
    public void resumeLoginFlow(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        request.setAttribute(FlowExecutionUrlSavingAction.BASE_FLOW_EXECUTION_KEY, session.getAttribute(FlowExecutionUrlSavingAction.BASE_FLOW_EXECUTION_KEY))
        session.removeAttribute(FlowExecutionUrlSavingAction.BASE_FLOW_EXECUTION_KEY)
        request.getRequestDispatcher("/WEB-INF/jsp/webflowContinue.jsp").forward(request, response)
    }
}
