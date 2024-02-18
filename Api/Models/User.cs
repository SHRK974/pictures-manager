using System;
using Api.Dtos;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using static Api.Controllers.UserController;

namespace Api.Models
{
	public class User
	{
        public User(UserAuthenticationDto user)
        {
            Email = user.Email;
            // crypt password
            Password = user.Password;
        }

        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }

        // Albums = Album AllImages + Album Deleted + Created Albums
        public List<MinimalAlbumInfoDto> Albums { get; set; } = new List<MinimalAlbumInfoDto>();

        // SharedWithMe = Albums from other 
        public List<MinimalAlbumInfoDto> SharedWithMe { get; set; } = new List<MinimalAlbumInfoDto>();

        public List<MinimalAlbumInfoDto> SharedWithOther { get; set; } = new List<MinimalAlbumInfoDto>();

    }
}

