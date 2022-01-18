package util.base

import io.qameta.allure.Step
import util.reporting.ScreenRecording

class RecordingHelper {

    @Step("Start recording")
    fun startRecording() {
        ScreenRecording().startRecording()
    }

    @Step("Stop recording")
    fun stopRecording(saveRecording: Boolean) {
        ScreenRecording().stopRecording(saveRecording = saveRecording)
    }
}
