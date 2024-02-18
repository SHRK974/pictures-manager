using System;
using System.Threading.Tasks;
using Api.Dtos;
using Api.Exceptions;
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
    public class ImageController : Controller
    {
        private readonly IHttpContextAccessor _httpContextAccessor;
        private readonly IImageService _imageService;
        private readonly IAuthenticationService _authenticationService;
        public readonly ILogger _logger;

        public ImageController(IImageService imageService, IAuthenticationService authenticationService, IHttpContextAccessor httpContextAccessor, ILogger<ImageController> logger)
        {
            _imageService = imageService;
            _authenticationService = authenticationService;
            _httpContextAccessor = httpContextAccessor;
            _logger = logger;
        }


        // FINAL ROUTES

        /// <summary>
        /// Get the image , if image not in album of user or in shared with me, unauthorized
        /// </summary>
        /// <returns>List of Albums and their images in base64</returns>
        /// Postman OK
        [HttpGet("{imageId}")]
        public async Task<IActionResult> GetImage(string imageId)
        {
            string actualRoute = _httpContextAccessor.HttpContext.Request.Path.Value;

            // Get the authorization token from the request headers
            string token = Request.Headers["Authorization"].ToString().Replace("Bearer ", "");

            // Decode the token to get the user ID
            UserTokenDto userToken = _authenticationService.DecodeToken(token);

            // Return Unauthorized if the user ID is null
            if (userToken.UserId == null)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute}");
                return Unauthorized();
            }

            try
            {
                // Try to get the image by ID
                ImageDto imageDto = await _imageService.GetAsync(imageId);

                _logger.LogInformation($"Request succeeded. Route: {actualRoute}");
                // Return the image if found
                return Ok(imageDto);
            }
            catch (ItemNotFoundException ex)
            {
                _logger.LogError($"Request Unauthorized. Route: {actualRoute} Reason : {ex.Message}");
                // Return NotFound if the image is not found
                return NotFound(new { errorMessage = ex.Message });
            }
        }
        //END

    }
}

