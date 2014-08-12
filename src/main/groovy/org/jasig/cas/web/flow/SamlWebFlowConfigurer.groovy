package org.jasig.cas.web.flow

import groovy.util.logging.Slf4j
import org.jasig.cas.service.IdpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.binding.expression.ognl.OgnlExpressionParser
import org.springframework.binding.expression.support.FluentParserContext
import org.springframework.binding.expression.support.LiteralExpression
import org.springframework.webflow.action.SetAction
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry
import org.springframework.webflow.engine.SubflowState
import org.springframework.webflow.engine.Transition
import org.springframework.webflow.engine.ViewState
import org.springframework.webflow.engine.builder.model.SubflowExpression
import org.springframework.webflow.engine.support.ActionTransitionCriteria
import org.springframework.webflow.engine.support.DefaultTargetStateResolver
import org.springframework.webflow.engine.support.DefaultTransitionCriteria
import org.springframework.webflow.engine.support.TransitionCriteriaChain
import org.springframework.webflow.execution.RequestContext

import javax.annotation.PostConstruct

@Slf4j
class SamlWebFlowConfigurer {
    @Autowired
    FlowDefinitionRegistry flowDefinitionRegistry

    @Autowired
    IdpService idpService

    @Autowired
    OgnlExpressionParser ognlExpressionParser

    @PostConstruct
    def setup() {
        log.debug("setting up super simple person service")
        this.setupWebFlow()
        log.debug("done setting up super simple person service")
    }

    private void setupWebFlow() {
        log.trace("setting up web flow")

        def flow = flowDefinitionRegistry.getFlowDefinition("login")
        ViewState viewLoginForm = flow.getState("viewLoginForm")

        idpService.idps.each { idp ->
            viewLoginForm.transitionSet.add(new Transition(new DefaultTransitionCriteria(new LiteralExpression(idp.code)), new DefaultTargetStateResolver("samlSubflow")).with {
                attributes.put("bind", false)
                attributes.put("validate", false)
                executionCriteria = new TransitionCriteriaChain().with {
                    add(new ActionTransitionCriteria(new SetAction(
                            ognlExpressionParser.parseExpression("requestScope.idpId", new FluentParserContext().evaluate(RequestContext)),
                            new LiteralExpression(idp.entityId)
                    )))
                    add(new ActionTransitionCriteria(new SetAction(
                            ognlExpressionParser.parseExpression("conversationScope.idpCode", new FluentParserContext().evaluate(RequestContext)),
                            new LiteralExpression(idp.code)
                    )))
                    it
                }
                it
            })
        }

        new SubflowState(flow, "samlSubflow", new SubflowExpression(new LiteralExpression("saml"), flowDefinitionRegistry)).with {
            ['warn', 'generateLoginTicket', 'sendTicketGrantingTicket'].each {
                transitionSet.add(new Transition(new DefaultTransitionCriteria(new LiteralExpression(it)), new DefaultTargetStateResolver(it)))
            }
        }

        log.trace("done setting up web flow")
    }
}
