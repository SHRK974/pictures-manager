using Api.Models;

namespace Api.Repositories.IRepositories
{
	public interface IUserRepository
	{
        public Task<List<User>> GetAsync();
        public Task<User?> GetAsync(string id);
        public Task<User?> GetByEmail(string email);
        public Task CreateAsync(User newUser);
        public Task UpdateAsync(string id, User updatedUser);
        public Task RemoveAsync(string id);
    }
}

