# wwoz_to_spotify

Scrape WWOZ Spinitron's RSS feed and add all songs to a Spotify playlist.

```bash
lein lambda deploy production
```

run locally

```bash
lein run
```

## Notes

I manually created a Spotify refresh token with `playlist-modify-public` scope. This refresh token essentially lasts forever and can be used to generate user-attached access tokens.

## RSS Feed Data

Not actually JSON, but still.

```json
{
  "author": null,
  "description": "The most recent songs played on WWOZ and logged on Spinitron, WWOZ's playlist handling service provider.",
  "language": "en-us",
  "link": "http://spinitron.com//radio/playlist.php?station=wwoz",
  "type": "rss_2.0",
  "published": "Sun Feb 25 21:40:57 CST 2018",
  "title": "Recent spins on WWOZ",
  "entries": [
    {
      "content": "'Few Bana Zambia' by Five Revolutions from Welcome To Zamrock! How Zambia's Liberation Led To A Rock Revolu  spun at 9:40pm CST Sun Feb 25th 2018 by WWOZ Programming on Spirits of Congo Square with Baba Geno, WWOZ New Orleans",
      "updated": null,
      "title": "Five Revolutions: 'Few Bana Zambia'",
      "author": "",
      "categories": [

      ],
      "link": "http://spinitron.com/radio/playlist.php?station=wwoz&plid=24413#468935",
      "id": "http://spinitron.com/radio/playlist.php?station=wwoz&plid=24413#468935",
      "content-type": null,
      "published": "Sun Feb 25 21:40:49 CST 2018"
    }
  ]
}
```

## To Do

- Find each song on Spotify
  - https://developer.spotify.com/web-api/search-item/
- Check the songs that are already on the playlist against the new songs
  - https://developer.spotify.com/web-api/get-playlists-tracks/
- Add only songs that are not already in playlist
  - https://developer.spotify.com/web-api/add-tracks-to-playlist/
- Use the link provided at the key `:id` to find which show the song came from.
