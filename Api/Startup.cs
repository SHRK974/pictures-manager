using System.Text;
using Api.Models;
using Api.Repositories;
using Api.Repositories.IRepositories;
using Api.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using MongoDB.Driver;
using Serilog;
using Serilog.Events;
using Swashbuckle.AspNetCore.SwaggerUI;

namespace Api
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            // Configure the database settings
            services.Configure<PictsManagerDatabaseSetting>(
                Configuration.GetSection("PictsManagerDatabase"));
            // Injecte instance of IMongoDatabase
            services.AddSingleton<IMongoDatabase>(provider =>
            {
                IOptions<PictsManagerDatabaseSetting>? pictsManagerDatabaseSetting = provider.GetService<IOptions<PictsManagerDatabaseSetting>>();
                if (pictsManagerDatabaseSetting == null)
                {
                    throw new ArgumentNullException(nameof(pictsManagerDatabaseSetting), "PictsManagerDatabaseSetting is not registered in the container.");
                }
                string mongoConnectionString = Environment.GetEnvironmentVariable("MONGO_CONNECTION_STRING") ?? pictsManagerDatabaseSetting.Value.ConnectionString;
                string mongoDatabaseName = Environment.GetEnvironmentVariable("MONGO_DATABASE_NAME") ?? pictsManagerDatabaseSetting.Value.DatabaseName;
                MongoClient mongoClient = new MongoClient(mongoConnectionString);
                return mongoClient.GetDatabase(mongoDatabaseName);
            });

            // Configure JWT Authentication
            services.AddSingleton(Configuration.GetValue<string>("JwtSecretKey"));
            services.AddAuthentication(x =>
            {
                x.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                x.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            }).AddJwtBearer(x =>
            {
                x.RequireHttpsMetadata = false;
                x.SaveToken = true;
                x.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(Configuration.GetValue<string>("JwtSecretKey"))),
                    ValidateIssuer = false,
                    ValidateAudience = false
                };
            });

            // Register repositories
            services.AddScoped<IUserRepository, UserRepository>();
            services.AddScoped<IImageRepository, ImageRepository>();
            services.AddScoped<IAlbumRepository, AlbumRepository>();

            // Register services
            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Information()
                .MinimumLevel.Override("Microsoft", LogEventLevel.Warning)
                .Enrich.FromLogContext()
                .WriteTo.File(Path.Combine(Directory.GetCurrentDirectory(), "Logs", "log.txt"), rollingInterval: RollingInterval.Day)
                .CreateLogger();

            services.AddLogging(loggingBuilder =>
            {
                loggingBuilder.AddSerilog(dispose: true);
            });


            services.AddScoped<IAlbumService,AlbumService>();
            services.AddScoped<IUserService,UserService>();
            services.AddScoped<IAuthenticationService, AuthenticationService>();
            services.AddScoped<IImageService, ImageService>();

            services.AddControllers();

            services.AddHttpContextAccessor();


            // Configure Swagger/OpenAPI
            services.AddSwaggerGen();

            // Configure routing
            services.AddRouting();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
                app.UseSwagger();
                app.UseSwaggerUI(c =>
                {
                    c.SwaggerEndpoint("/swagger/v1/swagger.json", "My API V1");
                    c.DocExpansion(DocExpansion.None);
                });
            }

            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseAuthentication();
            app.UseAuthorization();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
