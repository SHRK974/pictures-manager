using System;
using Api.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Api.Repositories;
using Api.Repositories.IRepositories;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Api.Dtos;
using Api.Exceptions;

namespace Api.Services
{
	public class UserService : IUserService
    {
        private string BASE_ALBUM_NAME = "All Images";
        private string BASE_ALBUM_NAME_DELETED = "Deleted";

        private readonly IUserRepository _userRepository;
        private readonly IAlbumRepository _albumRepository;

        private readonly string _key;

        public UserService(IUserRepository userRepository,IAlbumRepository albumRepository,string jwtKey)
        {
            _userRepository = userRepository;
            _albumRepository = albumRepository;
            _key = jwtKey;
        }

        public UserService()
        {
            // This parameterless constructor is needed for Moq to create a proxy for the class
        }

        public async Task<User?> GetAsync(string id) {
            return await _userRepository.GetAsync(id);
        }

        public async Task<List<User>> GetAsync()
        {
            return await _userRepository.GetAsync();
        }

        public async Task CreateAsync(UserAuthenticationDto userAuthenticationDto)
        {
            User newUser = new User(userAuthenticationDto);

            Album allImages = new Album ( BASE_ALBUM_NAME, false);
            Album deletedImages = new Album (BASE_ALBUM_NAME_DELETED, false);

            await _albumRepository.CreateAsync(allImages);
            if (allImages.Id == null) throw new NullReferenceException($"Fail create Album {BASE_ALBUM_NAME}");

            await _albumRepository.CreateAsync(deletedImages);
            if (deletedImages.Id == null) throw new NullReferenceException($"Fail create Album {BASE_ALBUM_NAME_DELETED}");

            newUser.Albums.Add(new MinimalAlbumInfoDto(allImages.Id, allImages.Label));
            newUser.Albums.Add(new MinimalAlbumInfoDto( deletedImages.Id, deletedImages.Label));

            await _userRepository.CreateAsync(newUser);
            if (newUser.Id == null) throw new NullReferenceException($"Fail create User {BASE_ALBUM_NAME_DELETED}");
        }

        public async Task<List<MinimalAlbumInfoDto>> GetAllUserAlbumsAsync(string userId) {
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            List<MinimalAlbumInfoDto> userAlbums = new List<MinimalAlbumInfoDto>();
            foreach (MinimalAlbumInfoDto item in user.Albums)
            {
                Album? album = await _albumRepository.GetAsync(item.Id);
                if (album == null || album.Id == null) throw new ItemNotFoundException(nameof(User));

                List<MinimalImageInfoDto> minimalImageInfoDtos = new List<MinimalImageInfoDto>();
                foreach (MinimalImageInfoDto image in album.Images)
                {
                    if (image.Id == null) throw new NullReferenceException("Fail to get image id");
                    minimalImageInfoDtos.Add(image);
                }

                userAlbums.Add(new MinimalAlbumInfoDto(album.Id, album.Label, minimalImageInfoDtos));
            }

            return userAlbums;
        }

        public async Task<List<MinimalAlbumInfoDto>> GetAlbumsSharedWithCurrentUserAsync(string userId) {
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            List<MinimalAlbumInfoDto> userAlbums = new List<MinimalAlbumInfoDto>();
            foreach (MinimalAlbumInfoDto item in user.SharedWithMe)
            {
                Album? album = await _albumRepository.GetAsync(item.Id);
                if (album == null || album.Id == null) throw new ItemNotFoundException(nameof(User));

                List<MinimalImageInfoDto> minimalImageInfoDtos = new List<MinimalImageInfoDto>();
                foreach (MinimalImageInfoDto image in album.Images)
                {
                    if (image.Id == null) throw new NullReferenceException("Fail to get image id");
                    minimalImageInfoDtos.Add(image);
                }

                userAlbums.Add(new MinimalAlbumInfoDto(album.Id, album.Label, minimalImageInfoDtos));
            }

            return userAlbums;
        }
        
        public async Task<List<MinimalAlbumInfoDto>> GetAlbumsSharedWithOtherUsersAsync(string userId) {
            User? user = await _userRepository.GetAsync(userId);
            if (user == null || user.Id == null) throw new ItemNotFoundException(nameof(User));

            List<MinimalAlbumInfoDto> userAlbums = new List<MinimalAlbumInfoDto>();
            foreach (MinimalAlbumInfoDto item in user.SharedWithOther)
            {
                Album? album = await _albumRepository.GetAsync(item.Id);
                if (album == null || album.Id == null) throw new ItemNotFoundException(nameof(User));

                List<MinimalImageInfoDto> minimalImageInfoDtos = new List<MinimalImageInfoDto>();
                foreach (MinimalImageInfoDto image in album.Images)
                {
                    if (image.Id == null) throw new NullReferenceException("Fail to get image id");
                    minimalImageInfoDtos.Add(image);
                }

                userAlbums.Add(new MinimalAlbumInfoDto(album.Id, album.Label, minimalImageInfoDtos));
            }

            return userAlbums;
        }

        public async Task ShareAlbumWithUser(string fromUserId,string albumId, string toUserId) {
            User? fromUser = await _userRepository.GetAsync(fromUserId);
            if (fromUser == null) throw new ItemNotFoundException(nameof(User));

            MinimalAlbumInfoDto? fromUserContainAlbum = fromUser.Albums.Find(x => x.Id == albumId);
            if (fromUserContainAlbum == null) throw new ItemNotFoundException(nameof(Album));

            User? toUser = await _userRepository.GetAsync(toUserId);
            if (toUser == null || toUser.Id == null) throw new ItemNotFoundException(nameof(User));
            toUser.SharedWithMe.Add(fromUserContainAlbum);
            fromUser.SharedWithOther.Add(fromUserContainAlbum);
            await _userRepository.UpdateAsync(toUser.Id, toUser);
            await _userRepository.UpdateAsync(fromUser.Id, fromUser);
        }

        public class SearchByEmailDto {
            public string Id { get; set; }
            public string Email { get; set; }
        }

        public async Task<List<SearchByEmailDto>> SearchUserByEmail(string? emailLike) {
           List<User> users = await _userRepository.GetAsync();

            List<User> filteredUsers = emailLike == null ? users : users.Where(u => u.Email.Contains(emailLike)).ToList();

            List<SearchByEmailDto> searchByEmailDtos = new List<SearchByEmailDto>();

            foreach (User item in filteredUsers)
            {
                if(item.Id == null) { throw new NullReferenceException(); }
                searchByEmailDtos.Add(new SearchByEmailDto { Email = item.Email, Id = item.Id });
            }

            return searchByEmailDtos;
        }


    }
}

