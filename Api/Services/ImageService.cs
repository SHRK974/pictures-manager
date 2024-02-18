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
	public class ImageService : IImageService
    {

        private readonly IImageRepository _imageRepository;

        public ImageService(IImageRepository imageRepository)
        {
             _imageRepository = imageRepository;
        }

        public ImageService()
        {
            // This parameterless constructor is needed for Moq to create a proxy for the class
        }

        public async Task CreateAsync(Image image) => await _imageRepository.CreateAsync(image);

        public async Task<ImageDto> GetAsync(string imageId)
        {
            Image? image = await _imageRepository.GetAsync(imageId);
            if (image == null || image.Id == null) throw new ItemNotFoundException(nameof(Image));

            return new ImageDto { Id = image.Id, Label = image.Label, Extension = image.Extension, Base64 = image.Base64, CompressedBase64 = image.CompressedBase64, CreatedAt = image.CreatedAt };
        }
    }
}

