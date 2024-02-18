using Api.Models;

namespace Api.Repositories.IRepositories
{
	public interface IImageRepository
	{
        public Task<List<Image>> GetAsync();
        public Task<Image?> GetAsync(string id);
        public Task CreateAsync(Image newImage);
        public Task UpdateAsync(string id, Image updatedImage);
        public Task RemoveAsync(string id);
    }
}

