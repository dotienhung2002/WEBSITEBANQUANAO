package com.application.fusamate.model;
import com.application.fusamate.dto.EmployeeDto;

public class JwtResponseModel {
    private final String access_token;
    private final String refresh_token;
    private final int status;
    private EmployeeDto employeeDto;

    public JwtResponseModel(String accessToken, String refreshToken, EmployeeDto employeeDto,int status) {
        this.access_token= accessToken;
        this.refresh_token = refreshToken;
        this.employeeDto = employeeDto;
        this.status=status;
    }
    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public EmployeeDto getInformation() {
        return employeeDto;
    }

    public int getStatus() {
        return status;
    }
}
