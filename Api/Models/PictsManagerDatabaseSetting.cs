using System;
namespace Api.Models
{
	public class PictsManagerDatabaseSetting
    {
		public PictsManagerDatabaseSetting()
		{
		}

        public string ConnectionString { get; set; } = null!;

        public string DatabaseName { get; set; } = null!;

        public string ImagesCollectionName { get; set; } = null!;

        public string UsersCollectionName { get; set; } = null!;

        public string AlbumsCollectionName { get; set; } = null!;

        
    }
}

