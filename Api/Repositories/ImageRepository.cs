﻿using System;
using Api.Models;
using Api.Repositories.IRepositories;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace Api.Repositories
{
	public class ImageRepository: IImageRepository
	{

        private readonly IMongoCollection<Image> _imagesCollection;


        public ImageRepository(IMongoDatabase mongoDatabase,IOptions<PictsManagerDatabaseSetting> databaseSettings)
        {
            _imagesCollection = mongoDatabase.GetCollection<Image>(databaseSettings.Value.ImagesCollectionName);
        }

        public async Task<List<Image>> GetAsync() =>
            await _imagesCollection.Find(_ => true).ToListAsync();

        public async Task<Image?> GetAsync(string id) =>
            await _imagesCollection.Find(x => x.Id == id).FirstOrDefaultAsync();

        public async Task CreateAsync(Image newImage) =>
            await _imagesCollection.InsertOneAsync(newImage);

        public async Task UpdateAsync(string id, Image updatedImage) =>
            await _imagesCollection.ReplaceOneAsync(x => x.Id == id, updatedImage);

        public async Task RemoveAsync(string id) =>
            await _imagesCollection.DeleteOneAsync(x => x.Id == id);
    }
}

