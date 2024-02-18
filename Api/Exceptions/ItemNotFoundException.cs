using System;
namespace Api.Exceptions
{
	public class ItemNotFoundException : Exception
	{
        private static string CapitalizeItem(string item)
        {
            return item.Substring(0, 1).ToUpper() + item.Substring(1).ToLower();
        }

        public ItemNotFoundException(string item) : base($"{CapitalizeItem(item)} not found")
        {
        }

        
    }
}

