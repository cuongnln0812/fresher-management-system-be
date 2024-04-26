package com.example.phase1_fams.auth;


import com.example.phase1_fams.dto.request.PasswordChangeReq;
import com.example.phase1_fams.dto.response.StatisticsRes;
import com.example.phase1_fams.dto.response.UserAuthRes;
import com.example.phase1_fams.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeReq request) {
        authenticationService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ResponseUtils.ok("Password changed successfully", HttpStatus.OK);
    }

    @GetMapping("/user-information")
    public ResponseEntity<?> getUserInfo() {
        UserAuthRes user = authenticationService.getUserInfo();
        return ResponseUtils.response(user, "User information retrieved successfully!", HttpStatus.OK);
    }

    @GetMapping("/homepage-statistics")
    public ResponseEntity<?> getStatistics() {
        StatisticsRes res = authenticationService.getAllStatistics();
        return ResponseUtils.response(res, "Get all statistic successfully!", HttpStatus.OK);
    }
}
