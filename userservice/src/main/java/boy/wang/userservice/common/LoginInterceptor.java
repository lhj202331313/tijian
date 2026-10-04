package boy.wang.userservice.common;

import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER_ID = "currentUserId";
    public static final String CURRENT_USERNAME = "currentUsername";

    private final JwtUtil jwtUtil;

    @Autowired
    public LoginInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResponseUtil.writeJson(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            ResponseUtil.writeJson(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            Object userId = claims.get("userId");
            Long uid;
            if (userId instanceof Integer) {
                uid = ((Integer) userId).longValue();
            } else {
                uid = (Long) userId;
            }
            request.setAttribute(CURRENT_USER_ID, uid);
            request.setAttribute(CURRENT_USERNAME, claims.get("username", String.class));
            return true;
        } catch (Exception e) {
            ResponseUtil.writeJson(response, ResultCode.UNAUTHORIZED);
            return false;
        }
    }
}
