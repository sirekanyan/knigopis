package me.vadik.knigopis.repository.cache

interface AvatarCache {
    var urls: Map<String, String?>
}

class AvatarCacheImpl : AvatarCache {
    override var urls = mapOf<String, String?>()
}