using Api.Dtos;
using Api.Models;
using static Api.Services.UserService;

namespace Api.Services
{
    public interface IUserService
    {
        Task<User?> GetAsync(string userId);
        Task<List<User>> GetAsync();
        Task<List<MinimalAlbumInfoDto>> GetAllUserAlbumsAsync(string userId);
        Task<List<MinimalAlbumInfoDto>> GetAlbumsSharedWithCurrentUserAsync(string userId);
        Task<List<MinimalAlbumInfoDto>> GetAlbumsSharedWithOtherUsersAsync(string userId);
        Task CreateAsync(UserAuthenticationDto user);
        Task ShareAlbumWithUser(string fromUserId, string albumId, string toUserId);
        Task<List<SearchByEmailDto>> SearchUserByEmail(string? emailLike);


    }
}

