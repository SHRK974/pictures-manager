using System;
using System.ComponentModel.DataAnnotations;

namespace Api.Dtos
{
	public class UserAuthenticationDto
	{
        [Required]
        [StringLength(50)]
        [EmailAddress]
        public string Email { get; set; }
        [Required]
        [StringLength(50)]
        [RegularExpression(@"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{10,}$",
        ErrorMessage = "Password must be at least 10 characters long and contain at least one uppercase letter, one lowercase letter, and one number.")]
        public string Password { get; set; }

        public UserAuthenticationDto(string email,string password)
		{
            this.Email = email;
            this.Password = password;
		}

	}
}

