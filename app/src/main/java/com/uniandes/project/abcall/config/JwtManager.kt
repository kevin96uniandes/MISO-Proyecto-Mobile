package com.uniandes.project.abcall.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts

class JwtManager {

    fun decodeJWT(token: String): Claims {
        val secretKey = "frase-secreta"

        return Jwts.parser()
            .setSigningKey(secretKey.toByteArray())
            .parseClaimsJws(token)
            .body
    }

}