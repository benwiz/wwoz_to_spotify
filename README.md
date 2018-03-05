# wwoz_to_spotify

Scrape WWOZ Spinitron's RSS feed and add all songs to a Spotify playlist.

My first clojure project.

<iframe src="https://open.spotify.com/embed/user/bwisialowski/playlist/3vjFwtIxnPkNXk0XWTj0wy" width="300" height="380" frameborder="0" allowtransparency="true"></iframe>

## Notes

- I manually created a Spotify refresh token with `playlist-modify-public` scope. This refresh token essentially lasts forever and can be used to generate user-attached access tokens.
- I decided that I don't care if there are duplicated because that's a better representation of what wwoz is playing. But I _do_ need to not re-run the same RSS feed data.

To deploy

```bash
lein lambda deploy production
```

To run locally

```bash
lein run
```

## Useful Stuff

RSS feed data.

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

Spotify Search results.

```json
{
    "tracks": {
        "href": "https://api.spotify.com/v1/search?query=Wynton+Marsalis+Bourbon+Street+Parade&type=track&market=US&offset=0&limit=1",
        "items": [
            {
                "disc_number": 1,
                "popularity": 26,
                "duration_ms": 347493,
                "name": "Bourbon Street Parade",
                "explicit": false,
                "type": "track",
                "external_urls": {
                    "spotify": "https://open.spotify.com/track/4nofCND7qmopOVUO0FE6bi"
                },
                "external_ids": {
                    "isrc": "USSM19921587"
                },
                "preview_url": "https://p.scdn.co/mp3-preview/754c5174ba047e6c7bb9bf09d8889ace0c95cbb4?cid=2adec837a4754068905e93a4b3a8c143",
                "track_number": 12,
                "id": "4nofCND7qmopOVUO0FE6bi",
                "available_markets": [
                    "..."
                ],
                "uri": "spotify:track:4nofCND7qmopOVUO0FE6bi",
                "artists": [
                    {
                        "external_urls": {
                            "spotify": "https://open.spotify.com/artist/375zxMmh2cSgUzFFnva0O7"
                        },
                        "href": "https://api.spotify.com/v1/artists/375zxMmh2cSgUzFFnva0O7",
                        "id": "375zxMmh2cSgUzFFnva0O7",
                        "name": "Wynton Marsalis",
                        "type": "artist",
                        "uri": "spotify:artist:375zxMmh2cSgUzFFnva0O7"
                    }
                ],
                "album": {
                    "album_type": "album",
                    "release_date": "1991-03-26",
                    "images": [
                        {
                            "height": 640,
                            "url": "https://i.scdn.co/image/60aa854462a7487f21b7cc8cf3e205717020bcf6",
                            "width": 640
                        },
                        {
                            "height": 300,
                            "url": "https://i.scdn.co/image/1a3acb9121c6aace9f2c0cac233a24386069489f",
                            "width": 300
                        },
                        {
                            "height": 64,
                            "url": "https://i.scdn.co/image/9eec6115d2721cec74ce321915aec0263a7a227e",
                            "width": 64
                        }
                    ],
                    "name": "Standard Time Vol. 2: Intimacy Calling",
                    "release_date_precision": "day",
                    "type": "album",
                    "external_urls": {
                        "spotify": "https://open.spotify.com/album/4cmRWv6XOmC9sRryBkeU8U"
                    },
                    "id": "4cmRWv6XOmC9sRryBkeU8U",
                    "available_markets": [
                        "..."
                    ],
                    "uri": "spotify:album:4cmRWv6XOmC9sRryBkeU8U",
                    "artists": [
                        {
                            "external_urls": {
                                "spotify": "https://open.spotify.com/artist/375zxMmh2cSgUzFFnva0O7"
                            },
                            "href": "https://api.spotify.com/v1/artists/375zxMmh2cSgUzFFnva0O7",
                            "id": "375zxMmh2cSgUzFFnva0O7",
                            "name": "Wynton Marsalis",
                            "type": "artist",
                            "uri": "spotify:artist:375zxMmh2cSgUzFFnva0O7"
                        }
                    ],
                    "href": "https://api.spotify.com/v1/albums/4cmRWv6XOmC9sRryBkeU8U"
                },
                "href": "https://api.spotify.com/v1/tracks/4nofCND7qmopOVUO0FE6bi"
            }
        ],
        "limit": 1,
        "next": null,
        "offset": 0,
        "previous": null,
        "total": 1
    }
}
```

## To Do

- Instead of sending only one track uri to Spotify at a time, all should be collected then used in a batch post.
  - But this likely won't be a huge savings since the intention is to run this job frequently.
