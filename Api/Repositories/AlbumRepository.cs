using Api.Models;
using Api.Repositories.IRepositories;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace Api.Repositories
{
	public class AlbumRepository : IAlbumRepository
	{

        private readonly IMongoCollection<Album> _albumsCollection;

        public AlbumRepository(IMongoDatabase mongoDatabase, IOptions<PictsManagerDatabaseSetting> databaseSettings)
        {
            _albumsCollection = mongoDatabase.GetCollection<Album>(databaseSettings.Value.AlbumsCollectionName);
        }

        public async Task<List<Album>> GetAsync() =>
            await _albumsCollection.Find(_ => true).ToListAsync();

        public async Task<Album?> GetAsync(string id) =>
            await _albumsCollection.Find(x => x.Id == id).FirstOrDefaultAsync();

        public async Task CreateAsync(Album newAlbum) {
            await _albumsCollection.InsertOneAsync(newAlbum);
        }

        public async Task UpdateAsync(string id, Album updatedAlbum) =>
            await _albumsCollection.ReplaceOneAsync(x => x.Id == id, updatedAlbum);

        public async Task RemoveAsync(string id) =>
            await _albumsCollection.DeleteOneAsync(x => x.Id == id);

    }
}

