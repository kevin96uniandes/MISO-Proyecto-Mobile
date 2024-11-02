package com.uniandes.project.abcall.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import javax.crypto.SecretKey

class JwtManager {

    fun decodeJWT(token: String): Claims {
        val secretKey: SecretKey = Keys.hmacShaKeyFor("E5P0Xc7J3deF5L5M8D9DjA3gW6F5Kz7g".toByteArray())

        return Jwts.parser()
            .clockSkewSeconds(15)
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

}