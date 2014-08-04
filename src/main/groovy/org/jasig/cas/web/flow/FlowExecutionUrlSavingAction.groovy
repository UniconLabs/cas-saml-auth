package org.jasig.cas.web.flow

import org.springframework.webflow.action.AbstractAction
import org.springframework.webflow.execution.Event
import org.springframework.webflow.execution.RequestContext

/**
 * {@link org.springframework.webflow.execution.Action} that saves the current state for retrieval after reentry from
 * external IDP authentication redirect.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class FlowExecutionUrlSavingAction extends AbstractAction {
    public static final String BASE_FLOW_EXECUTION_KEY = "flowExecutionKey"

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        def key = context.flowExecutionContext.key.toString()
        context.externalContext.sessionMap.put(BASE_FLOW_EXECUTION_KEY, key)
        return null
    }
}
