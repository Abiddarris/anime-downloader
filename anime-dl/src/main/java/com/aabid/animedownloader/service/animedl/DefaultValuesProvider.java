package com.aabid.animedownloader.service.animedl;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.EpisodeInfo;

public class DefaultValuesProvider implements ValuesProvider {

    @Override
    @NonNull
    public Map<String, Object> getValues(@NonNull EpisodeInfo info, @NonNull Selection selection) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", info.getAnilistId());
        values.put("episode", info.getEpisode());
        values.put("anime_title", info.getAnimeTitle());
        values.put("tryembed_anime_title", info.getAnimeTitle());
        values.put("ext", "%(ext)s");
        values.put("server_name", selection.getServerInfo().getName());
        values.put("server_id", selection.getServerInfo().getId());
        values.put("quality", selection.getQuality().getName());

        return values;
    }
}