package com.example.examcracker

class ModelPdf {

    var uid : String = ""
      var id : String = ""
      var description : String = ""
      var title : String = ""
      var categoryId : String = ""
      var url : String = ""
      var timestamp : Long = 0
      var viewsCount : Long = 0
    var downloadsCount: Long = 0

    constructor()
    constructor(
        uid: String,
        id: String,
        description: String,
        title: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        viewsCount: Long,
        downloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.description = description
        this.title = title
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount
    }

}