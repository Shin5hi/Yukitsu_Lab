package com.example.data.model

data class AnimeLora(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val triggerWord: String = "<lora:custom:1.0>",
    val primaryStyle: String = "shonen",
    val secondaryStyle: String = "ghibli",
    val styleWeights: Map<String, Float> = mapOf("shonen" to 0.6f, "ghibli" to 0.4f),
    val trainingSteps: Int = 1200,
    val trainingStatus: String = "completed", // training, completed, ready
    val sampleImagesCount: Int = 18,
    val description: String = "",
    val baseTriggerPrompt: String = ""
)
