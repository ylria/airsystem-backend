package pl.edu.agh.airsystem.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.edu.agh.airsystem.model.api.security.JWTToken;
import pl.edu.agh.airsystem.model.database.Client;
import pl.edu.agh.airsystem.model.database.StationClient;
import pl.edu.agh.airsystem.model.database.UserClient;

import java.util.Date;
import java.util.function.Function;


@Component
public class JWTTokenUtil {

    @Value("${airella.authorization.jwt.access_token_station_expiration_time_seconds}")
    private long jwtTokenStationExpirationTime;

    @Value("${airella.authorization.jwt.access_token_user_expiration_time_seconds}")
    private long jwtTokenUserExpirationTime;

    @Value("${airella.authorization.jwt.secret}")
    private String secret;


    public long getClientIdFromToken(String token) {
        return Long.parseLong(getClaimFromToken(token, Claims::getSubject));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public JWTToken generateAccessToken(Client client) {
        long expirationTimeInSeconds;
        if (client instanceof StationClient) {
            expirationTimeInSeconds = jwtTokenStationExpirationTime;
        } else {
            expirationTimeInSeconds = jwtTokenUserExpirationTime;
        }

        Claims claims = Jwts.claims().setSubject(String.valueOf(client.getId()));
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return new JWTToken(token, expirationDate);
    }

}