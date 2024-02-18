using System;
using System.Security.Claims;

namespace Api.Dtos
{
	public class ImageDto
	{
        public string Id { get; set; } = string.Empty;
        public string Label { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public string Extension { get; set; } = string.Empty;
        public string Base64 { get; set; } = string.Empty;
        public string CompressedBase64 { get; set; } = string.Empty;

        public ImageDto() { }

    }
}

