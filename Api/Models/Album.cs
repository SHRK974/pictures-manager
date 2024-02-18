using System;
using Api.Dtos;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Api.Models
{
	public class Album
	{
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }
        public string Label { get; set; }
        public List<MinimalImageInfoDto> Images { get; set; } = new List<MinimalImageInfoDto>();
        public bool CanDelete { get; set; } = true;

        public Album(string label)
		{
			this.Label = label;
		}

        public Album(string label, bool canDelete)
        {
            this.Label = label;
            this.CanDelete = canDelete;
        }

	}
}

