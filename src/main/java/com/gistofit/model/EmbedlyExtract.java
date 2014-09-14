package com.gistofit.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class EmbedlyExtract {
	@Getter
	@Setter
	public String ProviderUrl;

	@Getter
	@Setter
	public ArrayList<Author> Authors;

	@Getter
	@Setter
	public String ProviderDisplay;

	@Getter
	@Setter
	public ArrayList<RelatedSite> Related;

	@Getter
	@Setter
	public String FaviconUrl;

	@Getter
	@Setter
	public ArrayList<Keyword> Keywords;

	@Getter
	@Setter
	public ArrayList<AppLink> AppLinks;

	@Getter
	@Setter
	public String OriginalUrl;

	@Getter
	@Setter
	public Object Media;

	@Getter
	@Setter
	public String Content;

	@Getter
	@Setter
	public ArrayList<Entity> Entities;

	@Getter
	@Setter
	public String ProviderName;

	@Getter
	@Setter
	public String Type;

	@Getter
	@Setter
	public String Description;

	@Getter
	@Setter
	public ArrayList<Embed> Embeds;

	@Getter
	@Setter
	public ArrayList<Image> Images;

	@Getter
	@Setter
	public String Safe;

	@Getter
	@Setter
	public String Offset;

	@Getter
	@Setter
	public int CacheAge;

	@Getter
	@Setter
	public String Lead;

	@Getter
	@Setter
	public String Language;

	@Getter
	@Setter
	public String Url;

	@Getter
	@Setter
	public String Title;

	@Getter
	@Setter
	public ArrayList<Color> FaviconColors;

	@Getter
	@Setter
	public String Published;

	public static class Author
	{
		@Getter @Setter String Url;
		@Getter @Setter String Name;
	}

	public static class RelatedSite
	{
		@Getter @Setter String Description;
		@Getter @Setter String Title;
		@Getter @Setter String Url; 
		@Getter @Setter int ThumbnailWidth;
		@Getter @Setter double Score;
		@Getter @Setter int ThumbnailHeight;
		@Getter @Setter String ThumbnailUrl;
	}

	public static class Keyword
	{
		@Getter @Setter String Name;
		@Getter @Setter int Score;
	}

	public static class AppLink
	{
	}

	public static class Entity
	{
		@Getter @Setter String Name;
		@Getter @Setter int Count;
	}

	public static class Embed
	{
		@Getter @Setter int Width;
		@Getter @Setter String Html;
		@Getter @Setter int Height;
	}

	public static class Image
	{
		@Getter @Setter int Width;
		@Getter @Setter String Url;
		@Getter @Setter int Height;
		@Getter @Setter String Caption;
		@Getter @Setter ArrayList<Color> Colors;	
		@Getter @Setter double Entropy;
		@Getter @Setter int Size;
	}

	public static class Color
	{
		@Getter @Setter double Weight;
		@Getter @Setter int[] Color;
	}
}



