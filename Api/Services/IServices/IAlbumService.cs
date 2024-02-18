using System;
using Api.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Api.Repositories;
using Api.Repositories.IRepositories;
using Api.Dtos;

namespace Api.Services
{
	public interface IAlbumService
    {
        Task CreateAlbumAsync(string userId, AlbumCreationDto album);
        Task<Album?> GetAsync(string id);
        Task<List<Album>> GetAsync();
        Task AddImageToAlbum(string userId, string imageId, string? albumId) ;
        Task RemoveImageFromAlbum(string userId, string imageId, string? albumId);
        Task ResetBaseAlbum(string userId);
    }
}

