using Api.Models;

namespace Api.Repositories.IRepositories
{
	public interface IAlbumRepository
	{
        public Task<List<Album>> GetAsync();
        public Task<Album?> GetAsync(string id);
        public Task CreateAsync(Album newAlbum);
        public Task UpdateAsync(string id, Album updatedAlbum);
        public Task RemoveAsync(string id);
    }
}

