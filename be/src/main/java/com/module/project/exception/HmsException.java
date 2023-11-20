
package com.module.project.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HmsException extends RuntimeException {

    private HmsResponse<Object> hmsResponse;

    //With only error code
    public HmsException(String errorCode, String message) {
        super(errorCode);
        this.setHmsErrCode(buildResponse(errorCode, message));
    }

    public HmsException(String errorCode, String message, Object errorData) {
        this(errorCode, message);
        this.hmsResponse.setData(errorData);
    }

    public HmsException setHmsErrCode(HmsResponse<Object> hmsResponse) {
        this.hmsResponse = hmsResponse;
        return this;
    }

    private HmsResponse<Object> buildResponse(String errorCode, String message) {
        HmsResponse<Object> response = new HmsResponse<>();
        response.setCode(errorCode);
        response.setMessage(message);
        return response;
    }
}
