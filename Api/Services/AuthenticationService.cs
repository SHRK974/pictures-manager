using System;
using Api.Dtos;
using Api.Models;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Api.Repositories.IRepositories;
using Api.Repositories;
using Api.Helpers;
using Api.Exceptions;

namespace Api.Services
{
	public class AuthenticationService: IAuthenticationService
	{
        private readonly IUserRepository _userRepository;
        private readonly string _jwtSecretKey;

        public AuthenticationService(IUserRepository userRepository, IConfiguration configuration)
        {
            _jwtSecretKey = configuration.GetValue<string>("JwtSecretKey");
            _userRepository = userRepository;
        }

        public AuthenticationService()
        {
            // This parameterless constructor is needed for Moq to create a proxy for the class
        }

        public async Task<string> Authenticate(UserAuthenticationDto userAuthInfo)
        {
            User? user = await _userRepository.GetByEmail(userAuthInfo.Email);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));


            return TokenHelper.CreateToken(_jwtSecretKey,user.Id);
        }

        public UserTokenDto DecodeToken(string token)
        {
            return TokenHelper.DecodeToken(_jwtSecretKey,token);
        }
    }
}

