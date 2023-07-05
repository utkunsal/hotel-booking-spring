package com.example.notUsed;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/greetings")
public class GreetingsController {

   /* @GetMapping
    public ResponseEntity<String> sayHello(@AuthenticationPrincipal OAuth2User principal){
        return ResponseEntity.ok("hello " + principal.getAttribute("name") + " " + principal.getAttribute("email"));
    }*/

    @GetMapping
    public ResponseEntity<String> sayHello(){
         return ResponseEntity.ok("hello");
    }

    @GetMapping("/secure")
    public ResponseEntity<String> sayHelloSecure(){
        return ResponseEntity.ok("hello secure");
    }
}
