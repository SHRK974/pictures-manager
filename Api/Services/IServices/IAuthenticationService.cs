using Api.Dtos;

namespace Api.Services
{
    public interface IAuthenticationService
	{
        Task<string> Authenticate(UserAuthenticationDto userAuthInfo);
        UserTokenDto DecodeToken(string token);
    }
}

