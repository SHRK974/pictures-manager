using Api.Dtos;
using Api.Helpers;
using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Api.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    public class AlbumController : Controller
    {
        private readonly IHttpContextAccessor _httpContextAccessor;
        private readonly IAlbumService _albumService;
        private readonly IAuthenticationService _authenticationService;
        private readonly IImageService _imageService;
        private readonly IUserService _userService;
        public readonly ILogger _logger;


        public AlbumController(IAlbumService albumService, IAuthenticationService authenticationService, IImageService imageService, IUserService userService, IHttpContextAccessor httpContextAccessor, ILogger<AlbumController> logger)
        {
            _albumService = albumService;
            _authenticationService = authenticationService;
            _userService = userService;
            _imageService = imageService;
            _httpContextAccessor = httpContextAccessor;
            _logger = logger;
        }

        // FINAL ROUTES

        [HttpPost("adminReset")]
        public async Task<IActionResult> ResetImage()
        {
            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                return Unauthorized();
            }

            await _albumService.ResetBaseAlbum(userToken.UserId);

            return Ok();
        }
        /// <summary>
        /// (Should be MOVE in image/upload) Upload an image, if no album is set, it put the image to the default "All Images" of the current user
        /// </summary>
        /// <param name="originalImageFile"></param>
        /// <param name="compressedImageFile"></param>
        /// <param name="albumId"></param>
        /// <returns></returns>
        [HttpPost("upload")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> UploadImage(
             [FromForm(Name = "originalImageFile")] IFormFile originalImageFile,
            [FromForm(Name = "compressedImageFile")] IFormFile compressedImageFile,
            //IFormFile originalImageFile,
            //IFormFile compressedImageFile,
            [FromQuery] string? albumId
            )
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized(new { errorMessage = "Token not valid" });
            }

            _logger.LogInformation($"Compressed image size: {compressedImageFile.Length} bytes");

            // Get the file bytes of the original image
            byte[] originalImageBytes;
            using (var ms = new MemoryStream())
            {
                originalImageFile.CopyTo(ms);
                originalImageBytes = ms.ToArray();
            }

            // Insert the original image into the database
            Image image = new Image();
            image.Extension = Path.GetExtension(originalImageFile.FileName);
            image.Label = Path.GetFileNameWithoutExtension(originalImageFile.FileName);
            image.Base64 = Convert.ToBase64String(originalImageBytes);

            byte[] compressedImageBytes;
            using (var ms = new MemoryStream())
            {
                compressedImageFile.CopyTo(ms);
                compressedImageBytes = ms.ToArray();
            }
            image.CompressedBase64 = Convert.ToBase64String(compressedImageBytes);

            _logger.LogInformation($"Compressed image base64: {image.CompressedBase64}");
            await _imageService.CreateAsync(image);
            await _albumService.AddImageToAlbum(userToken.UserId, image.Id, albumId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(new { message = "Success" });
        }

        /// <summary>
        /// Remove an Image from an Album
        /// If the album is AllImages it put the image to Deleted Album
        /// If the album is Deleted it put the image to AllImages
        /// </summary>
        /// <param name="albumId"></param>
        /// <param name="imageId"></param>
        /// <returns></returns>
        [HttpDelete("{albumId}/{imageId}")]
        public async Task<IActionResult> RemoveImageFromAlbum(string albumId, string imageId)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            await _albumService.RemoveImageFromAlbum(userToken.UserId, imageId, albumId);

            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(new {message = "success"});
        }

        /// <summary>
        /// Add an image to an album
        /// </summary>
        /// <param name="albumId"></param>
        /// <param name="imageId"></param>
        /// <returns></returns>
        [HttpPost("{albumId}/{imageId}")]
        public async Task<IActionResult> AddImageToAlbum(string albumId, string imageId)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            await _albumService.AddImageToAlbum(userToken.UserId, imageId, albumId);
            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(new { message = "success" });
        }

        /// <summary>
        /// Create an album for the associated user
        /// </summary>
        /// <param name="album"></param>
        /// <returns></returns>
        [HttpPost]
        public async Task<IActionResult> CreateAlbum([FromBody] AlbumCreationDto album)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            if (!ModelState.IsValid)
            {
                _logger.LogError($"Request Bad Request. Route: {actualRoute}");
                return BadRequest(ModelState);
            }

            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            await _albumService.CreateAlbumAsync(userToken.UserId, album);
            _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
            return Ok(new { message = "success" });
        }

        
        //ENDS

    }
}

