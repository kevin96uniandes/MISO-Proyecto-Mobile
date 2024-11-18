package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Board
import com.uniandes.project.abcall.repositories.rest.BoardSummaryClient

class BoardSummaryViewModel(
    private val boardSummaryClient: BoardSummaryClient
): ViewModel() {

    private val _result = MutableLiveData<ApiResult<BoardSummaryClient.BoardSummaryResponse>>()
    val result: LiveData<ApiResult<BoardSummaryClient.BoardSummaryResponse>> get() = _result

    fun getBoardSummary(boardSummary: Board) {
        boardSummaryClient.getBoardSummary(boardSummary.toBoardSummaryRequestBody()) { response ->
            _result.value = response
        }
    }
}

private fun Board.toBoardSummaryRequestBody() =
    BoardSummaryClient.BoardSummaryRequestBody(
        channelId = this.channelId,
        stateId = this.stateId,
        startDate = this.startDate,
        endDate = this.endDate
    )