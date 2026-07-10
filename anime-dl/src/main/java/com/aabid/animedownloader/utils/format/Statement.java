package com.aabid.animedownloader.utils.format;

import java.util.Map;

interface Statement {
    String evaluate(Map<String, Object> values);
}
