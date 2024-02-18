using System;
using System.Threading.Tasks;
using Api.Dtos;
using Api.Exceptions;
using Api.Helpers;
using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using static Api.Services.UserService;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Api.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    public class UserController : Controller
    {
        private readonly IHttpContextAccessor _httpContextAccessor;
        private readonly IUserService _userService;
        private readonly IAuthenticationService _authenticationService;
        private readonly ILogger _logger;

        public UserController(IUserService userService, IAuthenticationService authenticationService, IHttpContextAccessor httpContextAccessor, ILogger<UserController> logger)
        {
            _userService = userService;
            _authenticationService = authenticationService;
            _httpContextAccessor = httpContextAccessor;
            _logger = logger;
        }

        public class TokenResponse {
            public string Token { get; set; }
            public TokenResponse(string token) { Token = token; }
        }

        /// <summary>
        /// Register User in db
        /// </summary>
        /// <param name="userAuthInfo"></param>
        /// <returns></returns>
        /// Postman OK
        [AllowAnonymous]
        [HttpPost("register")]
        public async Task<IActionResult> RegisterUser([FromBody] UserAuthenticationDto userAuthInfo)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = string.Empty;

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            try
            {
                await _userService.CreateAsync(userAuthInfo);
                token = await _authenticationService.Authenticate(userAuthInfo);
                if (string.IsNullOrWhiteSpace(token))
                {
                    return Unauthorized();
                }

                _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
                return Ok(new TokenResponse(token));
            }
            catch (ArgumentException ex) // Email Already in db
            {
                _logger.LogError($"Request Fail. Route: {actualRoute} Reason : {ex.Message}");
                return BadRequest(new { errorMessage = ex.Message });
            }
            catch (NullReferenceException ex) // Fail To Create User or Albums
            {
                _logger.LogError($"Request Fail. Route: {actualRoute} Reason : {ex.Message}");
                return BadRequest(new { errorMessage = ex.Message });
            }
            catch (ItemNotFoundException ex) // User doesnt exists in db (Authentication)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute} Reason : User {userAuthInfo.Email} not found");
                return Unauthorized( new { errorMessage = $"User {userAuthInfo.Email} not found" });
            }
            catch (Exception ex) // Other
            {
                return BadRequest(new { errorMessage = ex.Message });
            }
        }

        // POST api/User/login
        /// <summary>
        /// Log the user
        /// </summary>
        /// <param name="userAuthInfo"></param>
        /// <returns>A token</returns>
        /// POSTMAN OK
        [AllowAnonymous]
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] UserAuthenticationDto userAuthInfo)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            if (!ModelState.IsValid)
            {
                _logger.LogError($"Request Bad Request. Route: {actualRoute} Reason : ");
                return BadRequest(ModelState);
            }

            try
            {
                string token = await _authenticationService.Authenticate(userAuthInfo);
                if (string.IsNullOrWhiteSpace(token))
                {
                    _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                    return Unauthorized();
                }

                _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
                return Ok(new TokenResponse(token));
            }
            catch (ItemNotFoundException)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute} Reason : User {userAuthInfo.Email} not found");
                return Unauthorized($"User {userAuthInfo.Email} not found");
            }
        }

        /// <summary>
        /// Get Current User albums
        /// </summary>
        /// <returns>List of Albums and their images in base64</returns>
        /// Postman OK
        [HttpGet("albums")]
        public async Task<IActionResult> GetCurrentUserAlbums()
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }
            List<MinimalAlbumInfoDto> userAlbums = await _userService.GetAllUserAlbumsAsync(userToken.UserId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(userAlbums);
        }

        /// <summary>
        /// Get Albums Shared With Current User
        /// </summary>
        /// <returns>List of Albums and their images in base64</returns>
        /// Postman OK
        [HttpGet("albums/sharedWithMe")]
        public async Task<IActionResult> GetAlbumsSharedWithCurrentUser()
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            List<MinimalAlbumInfoDto> userAlbums = await _userService.GetAlbumsSharedWithCurrentUserAsync(userToken.UserId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(userAlbums);
        }

        /// <summary>
        /// Get Albums Shared With Other Users
        /// </summary>
        /// <returns>List of Albums and their images in base64</returns>
        /// Postman OK
        [HttpGet("albums/sharedWithOthers")]
        public async Task<IActionResult> GetAlbumsSharedWithOthers()
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            List<MinimalAlbumInfoDto> userAlbums = await _userService.GetAlbumsSharedWithOtherUsersAsync(userToken.UserId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(userAlbums);
        }

        // FINAL ROUTES
        //Cherche par email
        [AllowAnonymous]
        [HttpGet("search/{emailLike}")]
        public async Task<IActionResult> GetUserByEmail(string emailLike) {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(await _userService.SearchUserByEmail(emailLike));
        }

        [HttpPost("albums/share/{albumId}/{toUserId}")]
        public async Task<IActionResult> ShareAlbumWithUser(string albumId,string toUserId) {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            try
            {
                await _userService.ShareAlbumWithUser(userToken.UserId, albumId, toUserId);
            }
            catch (ItemNotFoundException exception)
            {
                _logger.LogError($"Request NotFound. Route: {actualRoute} Reason: Specified Album wasn't found in User's albums");
                return NotFound(new { errorMessage = "Specified Album wasn't found in User's albums" });
            }
            catch (Exception ex)
            {

            }

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(new {message = "success"});
        }
        // END


        //// GET api/User/me with token
        [HttpGet("me")]
        public async Task<IActionResult> GetCurrentUserInfo()
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            User? user = await _userService.GetAsync(userToken.UserId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(user);
        }

    }
}

