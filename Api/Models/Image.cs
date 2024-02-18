using System;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Api.Models
{
	public class Image
	{
		public Image()
		{
		}

        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }
        public string Label { get; set; } = string.Empty;
        public string Extension { get; set; } = string.Empty;
        public string Base64 { get; set; } = string.Empty;
        public string CompressedBase64 { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
		public DateTime? DeleteAt { get; set; }
	}
}

