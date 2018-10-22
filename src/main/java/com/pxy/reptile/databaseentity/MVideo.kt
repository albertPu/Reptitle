package com.pxy.reptile.databaseentity


object MVideo : BaseEntity() {
    var title = text("title").nullable()
    var coverImageUrl = text("cover_image_url").nullable()
    var playerUrlOne = text("player_url_one").nullable()
    var playerUrlTow = text("address").nullable()
    var hostType = integer("host_type").nullable()
}