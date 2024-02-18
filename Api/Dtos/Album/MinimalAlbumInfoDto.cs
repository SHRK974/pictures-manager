using System;
using System.Security.Claims;

namespace Api.Dtos
{
    public class MinimalAlbumInfoDto
    {
        public string Id { get; set; }
        public string Label { get; set; }
        public List<MinimalImageInfoDto> Images { get; set; } = new List<MinimalImageInfoDto>();

        public MinimalAlbumInfoDto(string id, string label){
            this.Id = id;
            this.Label = label;
        }

        public MinimalAlbumInfoDto(string id, string label, List<MinimalImageInfoDto> images) {
            this.Id = id;
            this.Label = label;
            this.Images = images;
        }
    }
}

