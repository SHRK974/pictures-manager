using System;
using Api.Models;
using Api.Repositories.IRepositories;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace Api.Repositories
{
	public class UserRepository: IUserRepository
	{

        private readonly IMongoCollection<User> _usersCollection;

        public UserRepository(IMongoDatabase mongoDatabase, IOptions<PictsManagerDatabaseSetting> databaseSettings)
        {
            _usersCollection = mongoDatabase.GetCollection<User>(databaseSettings.Value.UsersCollectionName);
        }

        public async Task<List<User>> GetAsync() =>
            await _usersCollection.Find(_ => true).ToListAsync();

        public async Task<User?> GetAsync(string id) =>
            await _usersCollection.Find(x => x.Id == id).FirstOrDefaultAsync();

        public async Task<User?> GetByEmail(string email) =>
            await _usersCollection.Find(x => x.Email == email).FirstOrDefaultAsync();

        public async Task CreateAsync(User newUser)
        {
            FilterDefinition<User> filter = Builders<User>.Filter.Eq(u => u.Email, newUser.Email);
            User existingUser = await _usersCollection.Find(filter).FirstOrDefaultAsync();

            if (existingUser != null)
            {
                throw new ArgumentException($"{nameof(newUser.Email)} already used");
            }

            await _usersCollection.InsertOneAsync(newUser);
        }

        public async Task UpdateAsync(string id, User updatedUser) =>
            await _usersCollection.ReplaceOneAsync(x => x.Id == id, updatedUser);

        public async Task RemoveAsync(string id) =>
            await _usersCollection.DeleteOneAsync(x => x.Id == id);

    }
}

