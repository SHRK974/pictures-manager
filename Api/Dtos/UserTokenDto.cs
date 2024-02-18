using System;
using System.Security.Claims;

namespace Api.Dtos
{
	public class UserTokenDto
	{
        public string UserId { get; set; } = string.Empty;

        public UserTokenDto(){}

    }
}

