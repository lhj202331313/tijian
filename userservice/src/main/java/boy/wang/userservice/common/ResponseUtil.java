package boy.wang.userservice.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeJson(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(resultCode);
        response.getWriter().write(MAPPER.writeValueAsString(result));
    }
}
