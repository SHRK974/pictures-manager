
using Api.Controllers;
using Api.Dtos;
using Api.Exceptions;
using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;
using static Api.Controllers.UserController;

namespace Api.Tests;

public class UserControllerTests: IDisposable
{

    private readonly Mock<IUserService> _userServiceMock;
    private readonly Mock<IAuthenticationService> _authenticationServiceMock;
    private readonly Mock<IHttpContextAccessor> _httpContextAccessor;
    private readonly UserController _userController;
    
    public UserControllerTests()
    {
        _userServiceMock = new Mock<IUserService>();
        _authenticationServiceMock = new Mock<IAuthenticationService>();
        _httpContextAccessor = new Mock<IHttpContextAccessor>();
        _userController = new UserController(_userServiceMock.Object, _authenticationServiceMock.Object, _httpContextAccessor.Object);
    }

    public void Dispose()
    {
        _userServiceMock.Reset();
        _authenticationServiceMock.Reset();
        _httpContextAccessor.Reset();
        _userController.Dispose();
    }

    [Fact]
    public async Task RegisterUser_ShouldReturnToken()
    {
        // Arrange
        string fakeToken = "fakeToken";
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto("email", "password");
        _userServiceMock.Setup(x => x.CreateAsync(userAuthenticationDto)).Verifiable();
        _authenticationServiceMock.Setup(x => x.Authenticate(userAuthenticationDto)).Returns(Task.FromResult<string>(fakeToken)).Verifiable();

        // Set up HttpContext with desired Request.Path.Value
        var httpContext = new DefaultHttpContext();
        httpContext.Request.Path = "/some/path";
        _httpContextAccessor.Setup(x => x.HttpContext).Returns(httpContext).Verifiable();

        var controllerContext = new ControllerContext { HttpContext = httpContext };
        _userController.ControllerContext = controllerContext;

        // Act
        IActionResult actionResult = await _userController.RegisterUser(userAuthenticationDto);

        // Assert
        OkObjectResult okResult = Assert.IsType<OkObjectResult>(actionResult);
        var data = okResult.Value as TokenResponse;

        _userServiceMock.Verify();
        _authenticationServiceMock.Verify();
        _httpContextAccessor.Verify();

        Assert.Equal(fakeToken, data.Token);
    }

    [Fact]
    public async Task RegisterUser_CreateDefaultAlbumForUserFailed_ShouldReturnBadRequest()
    {
        // Arrange
        string fakeToken = "fakeToken";
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto("email", "password");
        _userServiceMock.Setup(x => x.CreateAsync(userAuthenticationDto)).ThrowsAsync(new NullReferenceException($"Fail create Album")).Verifiable();
        _authenticationServiceMock.Setup(x => x.Authenticate(userAuthenticationDto)).Returns(Task.FromResult<string>(fakeToken)).Verifiable();

        // Set up HttpContext with desired Request.Path.Value
        var httpContext = new DefaultHttpContext();
        httpContext.Request.Path = "/some/path";
        _httpContextAccessor.Setup(x => x.HttpContext).Returns(httpContext).Verifiable();

        var controllerContext = new ControllerContext { HttpContext = httpContext };
        _userController.ControllerContext = controllerContext;

        // Act
        IActionResult actionResult = await _userController.RegisterUser(userAuthenticationDto);

        // Assert
        var result = Assert.IsType<BadRequestObjectResult>(actionResult);

        _userServiceMock.Verify();
        _httpContextAccessor.Verify();
    }

    [Fact]
    public async Task RegisterUser_AuthenticatedUserNotFoundInDb_ShouldReturnUnauthorized()
    {
        // Arrange
        string fakeToken = "fakeToken";
        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto("email", "password");
        _userServiceMock.Setup(x => x.CreateAsync(userAuthenticationDto)).Verifiable();
        _authenticationServiceMock.Setup(x => x.Authenticate(userAuthenticationDto)).ThrowsAsync(new ItemNotFoundException(nameof(User))).Verifiable();

        // Set up HttpContext with desired Request.Path.Value
        var httpContext = new DefaultHttpContext();
        httpContext.Request.Path = "/some/path";
        _httpContextAccessor.Setup(x => x.HttpContext).Returns(httpContext).Verifiable();

        var controllerContext = new ControllerContext { HttpContext = httpContext };
        _userController.ControllerContext = controllerContext;

        // Act
        IActionResult actionResult = await _userController.RegisterUser(userAuthenticationDto);

        // Assert
        var result = Assert.IsType<UnauthorizedObjectResult>(actionResult);

        _userServiceMock.Verify();
        _authenticationServiceMock.Verify();
        _httpContextAccessor.Verify();
    }
}

