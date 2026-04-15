package xyz.fiwka.budget.dataservice.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort

@Component
class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader.substring(7)
        val username = jwtTokenService.extractUsername(jwtToken)

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val user = findUserByLoginOutputPort.execute(username)

            if (user != null && jwtTokenService.isTokenValid(jwtToken, user)) {
                val authentication = UsernamePasswordAuthenticationToken(
                    user.username,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}

