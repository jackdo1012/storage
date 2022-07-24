package com.jackdo.storage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    
    @GetMapping()
    public ResponseEntity<Object> auth(HttpServletRequest req) {
        boolean auth = (boolean) req.getAttribute("auth");
        if (auth) {
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
