using System;
using System.Security.Claims;

namespace Api.Dtos
{
    public class MinimalImageInfoDto
    {
        public string Id { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        public MinimalImageInfoDto(string id, DateTime createdAt) {
            Id = id;
            CreatedAt = CreatedAt;
        }
    }
}

