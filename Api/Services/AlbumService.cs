using System;
using Api.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Api.Repositories;
using Api.Repositories.IRepositories;
using Api.Dtos;
using Api.Exceptions;

namespace Api.Services
{
	public class AlbumService : IAlbumService
    {
        private string BASE_ALBUM_NAME = "All Images";
        private string BASE_ALBUM_NAME_DELETED = "Deleted";

        private readonly IImageRepository _imageRepository;
        private readonly IAlbumRepository _albumRepository;
        private readonly IUserRepository _userRepository;

        public AlbumService(IImageRepository imageRepository, IAlbumRepository albumRepository, IUserRepository userRepository)
        {
             _imageRepository = imageRepository;
             _albumRepository = albumRepository;
             _userRepository = userRepository;
        }

        public AlbumService()
        {
            // This parameterless constructor is needed for Moq to create a proxy for the class
        }

        public async Task CreateAlbumAsync(string userId, AlbumCreationDto albumCreationDto) {
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            Album album = new Album(albumCreationDto.Label, albumCreationDto.CanDelete);
            await _albumRepository.CreateAsync(album);
            if (album == null || album.Id == null) throw new NullReferenceException("Fail to get Album Id");

            user.Albums.Add(new MinimalAlbumInfoDto(album.Id, album.Label));
            await _userRepository.UpdateAsync(user.Id, user);
        }

        public async Task<Album?> GetAsync(string id) {
            return await _albumRepository.GetAsync(id);
        }

        public async Task<List<Album>> GetAsync()
        {
            return await _albumRepository.GetAsync();
        }

        public async Task AddImageToAlbum(string userId, string imageId, string? albumId) {

            // Find the user
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            MinimalAlbumInfoDto? minimalAlbumInfoDto = albumId == null ? user.Albums.Find(x => x.Label == BASE_ALBUM_NAME) : user.Albums.FirstOrDefault(x => x.Id == albumId);
            if (minimalAlbumInfoDto == null || minimalAlbumInfoDto.Id == null) { throw new ItemNotFoundException(nameof(Album)); }

            // Find the Album
            Album? album = await _albumRepository.GetAsync(minimalAlbumInfoDto.Id);
            if (album == null || album.Id == null) { throw new ItemNotFoundException(nameof(Album)); }

            // Add Image to album
            Image? imageToAdd = await _imageRepository.GetAsync(imageId);
            if (imageToAdd == null) { throw new ItemNotFoundException(nameof(Image)); }
            MinimalImageInfoDto minimalImageToAddInfoDto = new MinimalImageInfoDto(imageToAdd.Id, imageToAdd.CreatedAt);

            album.Images.Add(minimalImageToAddInfoDto);

            // if the image is from album deleted => image should be deleted from album deleted
            MinimalAlbumInfoDto? minimalAlbumInfoDtoDeleted = user.Albums.Find(x => x.Label == BASE_ALBUM_NAME_DELETED);
            if (minimalAlbumInfoDtoDeleted == null || minimalAlbumInfoDtoDeleted.Id == null) { throw new Exception(nameof(Album)); }
            Album? albumDeleted = await _albumRepository.GetAsync(minimalAlbumInfoDto.Id);
            if (albumDeleted == null || albumDeleted.Id == null) { throw new ItemNotFoundException(nameof(Album)); }

            if (albumDeleted.Images.FirstOrDefault(x => x.Id == imageId) != null) {
                albumDeleted.Images.Remove(minimalImageToAddInfoDto);
                await _albumRepository.UpdateAsync(albumDeleted.Id, albumDeleted);
            }

            // Save Album in DB
            await _albumRepository.UpdateAsync(album.Id, album);
        }

        public async Task RemoveImageFromAlbum(string userId , string imageId, string? albumId)
        {
            // Find the user
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            MinimalAlbumInfoDto? minimalAlbumInfoDto = albumId == null ? user.Albums.Find(x => x.Label == BASE_ALBUM_NAME) : user.Albums.FirstOrDefault(x => x.Id == albumId);
            if (minimalAlbumInfoDto == null || minimalAlbumInfoDto.Id == null) { throw new ItemNotFoundException(nameof(Album)); }

            // Find the Album
            Album? album = await _albumRepository.GetAsync(minimalAlbumInfoDto.Id);
            if (album == null || album.Id == null) { throw new ItemNotFoundException(nameof(Album)); }

            // Remove Image from album
            MinimalImageInfoDto? imageToRemove = album.Images.Find(image => image.Id == imageId);
            if (imageToRemove == null || imageToRemove.Id == null) { throw new ItemNotFoundException(nameof(Image)); }

            album.Images.Remove(imageToRemove);

            // if the album is AllImage => image should be add to deleted
            //if(albumId == user.Albums.Find(x => x.Label == BASE_ALBUM_NAME).Id)
            //{

            //}

            // Save Album in DB
            await _albumRepository.UpdateAsync(album.Id, album);
        }

        public async Task ResetBaseAlbum(string userId) {
            //User? user = await _userRepository.GetAsync(userId);
            //if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            //string albumId = user.Albums.Find(x => x.Label == "All Images").Id;

            //Album? album = await _albumRepository.GetAsync(albumId);

            //Image? image = album.Images.FirstOrDefault();
            //if (image == null) throw new ItemNotFoundException(nameof(Image));

            //album.Images.Clear();

            //// Create a single Random instance outside the loop to avoid seed repetition
            //Random random = new Random();

            //for (int i = 0; i < 15; i++)
            //{
            //    // Create a new Image object and copy the properties from the original object
            //    Image modifiedImage = new Image
            //    {
            //        Base64 = image.Base64,
            //        CreatedAt = image.CreatedAt,
            //        Extension = image.Extension,
            //        Id = image.Id
            //    };

            //    // Generate a random number of days to subtract from the CreatedAt date
            //    int daysToRemove = random.Next(1, 30);

            //    // Subtract the days from the CreatedAt date
            //    modifiedImage.CreatedAt = image.CreatedAt.AddDays(-daysToRemove);

            //    // Generate a random hexadecimal digit to replace the last character of the Id property
            //    char randomHexDigit = random.Next(0, 16).ToString("X")[0];
            //    modifiedImage.Id = image.Id.Substring(0, image.Id.Length - 1) + randomHexDigit;

            //    // Add the modified image object to the album's Images list
            //    album.Images.Add(modifiedImage);
        }


    }
}

