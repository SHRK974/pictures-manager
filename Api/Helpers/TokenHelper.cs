using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Api.Dtos;
using Api.Models;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.IdentityModel.Tokens;

namespace Api.Helpers
{
    public static class TokenHelper
    {
		public static string CreateToken(string jwtSecretKey, string userId) {
            JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();
            SecurityToken token = tokenHandler.CreateToken(new SecurityTokenDescriptor()
            {
                Subject = new ClaimsIdentity(new Claim[] {
                    // Ajoute l'email au JWT
                    new Claim(ClaimTypes.NameIdentifier, userId)
                }),

                Expires = DateTime.UtcNow.AddDays(1),

                SigningCredentials = new SigningCredentials(
                    new SymmetricSecurityKey(Encoding.ASCII.GetBytes(jwtSecretKey)),
                    SecurityAlgorithms.HmacSha256Signature
                )
            });
            return tokenHandler.WriteToken(token);
        }

        public static string ExtractUserId(string jwtSecretKey, string token) {
            var key = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(jwtSecretKey));
            var tokenHandler = new JwtSecurityTokenHandler();
            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = key,
                ValidateIssuer = false,
                ValidateAudience = false
            };
            SecurityToken validatedToken;
            ClaimsPrincipal claims = tokenHandler.ValidateToken(token, validationParameters, out validatedToken);

            string? userId = claims.FindFirstValue(ClaimTypes.NameIdentifier);

            if (userId == null) { throw new NotImplementedException(); }

            return userId;
        }

        public static UserTokenDto DecodeToken(string jwtSecretKey, string token) {
            return new UserTokenDto { UserId = ExtractUserId(jwtSecretKey, token) };
        }
	}
}

