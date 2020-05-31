# wwoz_to_spotify

## What

This cronjob manages a [Spotify playlist](https://open.spotify.com/playlist/5P6WEbhcUsmXB08owijHYd?si=qAxkhFD3Q8WRfrjBY__N2g) that reflects WWOZ's most recently played songs.

## How

Scrape [WWOZ's Spinitron webpage](https://spinitron.com/WWOZ/) and add all new tracks to a [Spotify playlist](https://open.spotify.com/playlist/5P6WEbhcUsmXB08owijHYd?si=qAxkhFD3Q8WRfrjBY__N2g).

Keeps the most recent 100 songs and removes anything older.

Deployed to AWS Lambda Function

## Notes

I manually created a Spotify refresh token with `playlist-modify-public` scope. This refresh token essentially lasts forever and can be used to generate user-attached access tokens. There is a step-by-step guide [here](https://benwiz.io/blog/create-spotify-refresh-token/).

To deploy. May need to set correct Java version using `export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)`.

```bash
lein lambda deploy production
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

- Deploy cloudwatch event cron with deploy command
- Deploy env vars with deploy command
- Script to clean up bucket (delete bucket)

- Figure out what return value should be

- Create a simple github pages with a spotify web player that can play this playlist (maybe rename project just WWOZ)
