package xyz.fiwka.budget.mcpserver.configuration

import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.mcpserver.tool.BudgetMcpTools

@Configuration
class McpToolConfiguration {

    @Bean
    fun budgetToolCallbacks(budgetMcpTools: BudgetMcpTools): ToolCallbackProvider =
        MethodToolCallbackProvider.builder()
            .toolObjects(budgetMcpTools)
            .build()
}

