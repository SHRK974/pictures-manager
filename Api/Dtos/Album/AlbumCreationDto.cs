using System;
using System.ComponentModel.DataAnnotations;
using System.Security.Claims;

namespace Api.Dtos
{
	public class AlbumCreationDto
	{
        [Required]
        [StringLength(50)]
        [RegularExpression("^[a-zA-Z0-9]*$", ErrorMessage = "Label can only contain letters and numbers.")]
        public string Label { get; set; } = string.Empty;
        public bool CanDelete { get; set; } = true;

        public AlbumCreationDto(){}

    }
}

