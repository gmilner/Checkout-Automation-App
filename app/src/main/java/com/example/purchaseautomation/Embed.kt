package com.example.purchaseautomation

data class Embed (
    var content: String? = null,
    var embeds: Array<embeds>,
    var attachments: String? = null
)

data class embeds (
        var color: String? = null,
        var fields: Array<fields>
)

data class fields (
            var name: String,
            var value: String
            )



