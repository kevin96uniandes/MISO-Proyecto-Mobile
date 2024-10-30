package com.uniandes.project.abcall.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.util.Date

class JwtManager {

    fun decodeJWT(token: String): Claims {
        val secretKey = "frase-secreta"
        val allowedClockSkewMillis = 15000L

        return Jwts.parser()
            .setSigningKey(secretKey.toByteArray())
            .setAllowedClockSkewSeconds(allowedClockSkewMillis / 1000)
            .parseClaimsJws(token)
            .body
    }

}