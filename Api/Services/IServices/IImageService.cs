using System;
using Api.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Api.Repositories;
using Api.Repositories.IRepositories;
using Api.Dtos;

namespace Api.Services
{
	public interface IImageService
    {
        Task CreateAsync(Image image);
        Task<ImageDto> GetAsync(string imageId);
    }
}

