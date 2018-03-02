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

## To Do

- Find each song on Spotify
  - https://developer.spotify.com/web-api/search-item/
- Check the songs that are already on the playlist against the new songs
  - https://developer.spotify.com/web-api/get-playlists-tracks/
- Add only songs that are not already in playlist
  - https://developer.spotify.com/web-api/add-tracks-to-playlist/
